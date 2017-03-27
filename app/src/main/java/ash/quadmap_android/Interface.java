package ash.quadmap_android;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;

public class Interface extends AppCompatActivity {

    private Location Home;
    private Location[] locations;
    BufferedWriter bw;
    public int mode = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Fragment map = new MapsActivity();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(mode == 1)
                        GotoPoint(new Location[]{Home});
                    else
                        GotoPoint(locations);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.map,map).addToBackStack(null).commit();
        Bundle bundle = getIntent().getExtras();
        bw = bundle.getParcelable("Writer");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_interface,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.point_follow){

        }
        else
            if(id == R.id.path_follow){

            }
        return super.onOptionsItemSelected(item);
    }

    public void setHome(Location location){
        Home = location;
        Log.i("Home","Updated current location");
    }
    public void GotoPoint(Location[] _location) throws IOException {
        Location location;
        if(mode == 1) {
            location = _location[0];
            Toast.makeText(this, "Going to next Point\n" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
            if (bw != null)
                bw.write("A"+","+location.getLatitude() + "," + location.getLongitude());
        }
        else {
            for (Location a_location : _location) {
                bw.write("B" + "," + a_location.getLatitude() + "," + a_location.getLongitude());
            }
            bw.write("X");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent i = new Intent(this,GPSProvider.class);
        stopService(i);
    }

    public int getMode(){
        return mode;
    }
}
