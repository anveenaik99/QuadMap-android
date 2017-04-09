package ash.quadmap_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class Interface extends AppCompatActivity {

    private Location Home;
    public int mode = 1;
    String IP;
    int port;
    Socket socket;
    PrintWriter out;
    boolean customHeight = false;
    double height = 2;
    Menu menu;
    int point_no;
    BufferedReader in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(out != null) {
                    out.print("LAND");
                    out.flush();
                    Toast.makeText(Interface.this, "Landing Quad", Toast.LENGTH_SHORT).show();
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.include,new MapsActivity()).commit();
        Bundle bundle = getIntent().getExtras();
        IP = bundle.getString("IP");
        port = bundle.getInt("Port");
        Client client = new Client(this,IP,port);
        ReaderClient reader = new ReaderClient(this,IP,port);
        try {
            client.execute();
            reader.execute();
            out = client.get();
            in = reader.get();
            //Toast.makeText(this, out.toString(), Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | ExecutionException e) {
            Toast.makeText(this, "Can't Connect to Server.", Toast.LENGTH_SHORT).show();
        }
        if(out != null) {
            Toast.makeText(this, "Successfully Connected to Server", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Failed to connect to Server.\n" +
                    "        Try Again !!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_interface,menu);
        this.menu = menu;
        menu.findItem(R.id.height).setTitle("Custom Height (Def "+height+"m)");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.point_follow){
            mode = 1;
            Toast.makeText(this, "Switched to Point Follow", Toast.LENGTH_SHORT).show();
            point_no = 0;
        }
        if(id == R.id.path_follow){
            mode = 2;
            Toast.makeText(this, "Switched to Path Follow", Toast.LENGTH_SHORT).show();
            point_no = 0;
        }
        if(id == R.id.Go_Home){
            if(out != null){
                Toast.makeText(this, "Quad coming back to your location.", Toast.LENGTH_SHORT).show();
                out.print("H"+Home.getLatitude()+","+Home.getLongitude()+","+this.height);
                out.flush();
            }
        }
        if(id == R.id.height){
            if(item.isChecked()){
                customHeight = false;
                item.setChecked(false);
            }
            else {
                item.setChecked(true);
                customHeight = true;
            }
        }
        if(id == R.id.default_height){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Set Height");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    height = Double.parseDouble(input.getText().toString());
                    menu.findItem(R.id.height).setTitle("Custom Height (Def "+height+"m)");
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setHome(Location location){
        Home = location;
        Log.i("Home","Updated current location");
    }
    public void GotoPoint(Location[] _location,Double[] heights) throws IOException {
        Location location;
        if(mode == 1) {
            location = _location[0];
            if (out != null) {
                if (customHeight) {
                    Toast.makeText(this, "Going to next Point\n" + location.getLatitude() + "," + location.getLongitude() + "," + heights[0], Toast.LENGTH_SHORT).show();
                    out.print("A" + location.getLatitude() + "," + location.getLongitude() + "," + heights[0]);
                } else {
                    Toast.makeText(this, "Going to next Point\n" + location.getLatitude() + "," + location.getLongitude() + "," + this.height, Toast.LENGTH_SHORT).show();
                    out.print("A" + location.getLatitude() + "," + location.getLongitude() + "," + this.height);
                }
                out.flush();
            }
        }
        else {
            Toast.makeText(this, "Writing Array to Server" , Toast.LENGTH_SHORT).show();
            if(out != null) {
                if(customHeight) {
                    int i = 0;
                    for (Location a_location : _location) {
                        out.print("B" + a_location.getLatitude() + "," + a_location.getLongitude() + "," + Double.toString(heights[i]));
                        out.flush();
                        i++;
                    }
                }
                else
                {
                    for (Location a_location : _location) {
                        out.print("B" + a_location.getLatitude() + "," + a_location.getLongitude() + "," + Double.toString(this.height));
                        out.flush();
                    }
                }
            }
            point_no = 0;
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

    public boolean getHeightStatus(){
        return customHeight;
    }
    void incPoint_no(){
        point_no++;
    }

    int getPoint_no(){
        return point_no;
    }

    BufferedReader getIn(){
        return in;
    }
}
