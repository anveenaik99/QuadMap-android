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
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;

public class Interface extends AppCompatActivity {

    private Location Home;
    BufferedWriter bw;
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
                    GotoPoint(Home);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.map,map).addToBackStack(null).commit();
        Bundle bundle = getIntent().getExtras();
        bw = bundle.getParcelable("Writer");
    }
    public void setHome(Location location){
        Home = location;
        Log.i("Home","Updated current location");
    }
    public void GotoPoint(Location location) throws IOException {
        Toast.makeText(this, "Going to next Point\n"+location.getLatitude()+","+location.getLongitude(), Toast.LENGTH_SHORT).show();
        if(bw != null)
            bw.write(location.getLatitude()+","+location.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent i = new Intent(this,GPSProvider.class);
        stopService(i);
    }
}
