package ash.quadmap_android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.TimeUnit;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location location;
    LatLng last_location = null;
    LocationRequest mLocationRequest;
    Marker lastOpened = null;
    List<LatLng> locations = new ArrayList<>();
    List<Double> heights = new ArrayList<>();
    boolean markercheck;
    Marker lastQuadMark;
    Handler UI = new Handler();
    int height = 100;
    int width = 100;
    BitmapDrawable bitmapdraw = null;

    Bitmap b;
    Bitmap smallMarker;
    public MapsActivity(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.activity_maps, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2*1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.quad);
        }
        b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                UI.post(new updater(UI));
                mMap.setMyLocationEnabled(true);
                LatLng current = new LatLng(22.3189734,87.3026368);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(current).zoom(17.0f).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.moveCamera(cameraUpdate);
                mMap.setOnInfoWindowClickListener(MapsActivity.this);
                mMap.setOnMarkerClickListener(MapsActivity.this);
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                        final double[] height = new double[1];
                        if(((Interface) getActivity()).getHeightStatus()) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Set Height for Marker");
                            final EditText input = new EditText(getContext());
                            builder.setView(input);
                            markercheck = false;

                            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    height[0] = Double.parseDouble(input.getText().toString());
                                    markercheck = true;
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    makeMarker(latLng,height[0]);
                                }
                            });
                            builder.show();
                        } else {
                            markercheck = true;
                        }
                        makeMarker(latLng,height[0]);
                       // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                });
            }
        });
        return v;
    }

    void makeMarker(LatLng latLng,double height){
        if(markercheck) {
            if (((Interface) getActivity()).getMode() == 1) {
                mMap.clear();
                last_location = null;
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("Tap to send quad");
                mMap.addMarker(options);
                locations.add(latLng);
                heights.add(height);
            } else {
                if(((Interface)getActivity()).getPoint_no() == 0) {
                    mMap.clear();
                    heights.clear();
                    last_location = null;
                }
                ((Interface)getActivity()).incPoint_no();
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title("Tap to send to selected waypoints")
                        .icon(getTextMarker(Integer.toString(((Interface)getActivity()).getPoint_no())));
                mMap.addMarker(options);
                locations.add(latLng);
                heights.add(height);
                if(last_location != null)
                    mMap.addPolyline(new PolylineOptions()
                            .add(last_location,latLng)
                            .width(5)
                            .color(Color.RED));
                last_location = latLng;
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("API", "Location services connected.");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        ((Interface)getActivity()).setHome(location);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("API", "Location services suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("API", "Location services failed.");
    }

    @Override
    public void onLocationChanged(Location _location) {
        location = _location;
        ((Interface)getActivity()).setHome(location);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

            if (((Interface) getActivity()).getMode() == 1) {
                Log.i("Marker", "Click Marker");
                lastOpened = marker;
                Snackbar snack = Snackbar.make(getView(), lastOpened.getPosition().latitude +
                        "," +
                        lastOpened.getPosition().longitude, Snackbar.LENGTH_LONG);
                lastOpened.showInfoWindow();
                snack.show();
            } else {
                marker.showInfoWindow();
            }
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(((Interface)getActivity()).getMode() == 1) {
            Location i = new Location(LocationManager.GPS_PROVIDER);
            i.setLatitude(marker.getPosition().latitude);
            i.setLongitude(marker.getPosition().longitude);
            Location point[] = {i};
            Double[] ht = heights.toArray(new Double[heights.size()]);
            try {
                ((Interface) getActivity()).GotoPoint(point, ht);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Info", "Info Marker");
            mMap.clear();
            heights.clear();
            last_location = null;
        }
        else{
            List<Location> points = new ArrayList<>();
            for(int i = 0; i < ((Interface)getActivity()).getPoint_no(); i++){
                Location loc = new Location(LocationManager.GPS_PROVIDER);
                loc.setLatitude((locations.get(i)).latitude);
                loc.setLongitude((locations.get(i)).longitude);
                points.add(loc);
            }
            Location[] points_array = (Location[]) points.toArray(new Location[points.size()]);
            Double[] ht = heights.toArray(new Double[heights.size()]);
            try {
                ((Interface) getActivity()).GotoPoint(points_array,ht);
                Log.i("Mode 2","Sending Data");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMap.clear();
            heights.clear();
            last_location = null;
            locations.clear();
        }
    }

    public BitmapDescriptor getTextMarker(String text) {

        Paint paint = new Paint();
    /* Set text size, color etc. as needed */
        paint.setTextSize(80);

        int width = (int)paint.measureText(text);
        int height = (int)paint.getTextSize();

        paint.setTextAlign(Paint.Align.CENTER);
        // Create a transparent bitmap as big as you need
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        // During development the following helps to see the full
        // drawing area:
        canvas.drawColor(0x50A0A0A0);
        // Start drawing into the canvas
        canvas.translate(width / 2f, height);
        canvas.drawText(text, 0, 0, paint);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(image);
        return icon;
    }

    class updater implements Runnable {
        final Handler mHandler;

        updater(Handler mHandler) {
            this.mHandler = mHandler;
        }

        @Override
        public void run() {
            mHandler.postDelayed(this,50);
            String GPS = null;
            GPS = ((Interface) getActivity()).getIn();
            Log.i("Debug","In Runnable");
            if (GPS != null) {
                Log.i("Reader", "GPS Recieved");
                String[] latlng = GPS.split(",");
                LatLng latLng = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
                if (lastQuadMark != null)
                    lastQuadMark.remove();
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                lastQuadMark = mMap.addMarker(options);
            }
        }
    }
}
