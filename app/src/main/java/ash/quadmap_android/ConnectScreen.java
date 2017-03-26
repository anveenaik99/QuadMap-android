package ash.quadmap_android;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.util.concurrent.ExecutionException;

public class ConnectScreen extends AppCompatActivity {

    MyReciever myReciever;
    private Button connect;
    EditText Port;
    EditText IP;
    String GPSData;
    BufferedWriter b = null;
    String host;
    int port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_screen);

        connect = (Button)findViewById(R.id.connect_btn);
        Port = (EditText) findViewById(R.id.Port);
        IP = (EditText) findViewById(R.id.IP);
        if(!runtime_permissions()){
            enableButton();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        myReciever = new MyReciever();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GPSProvider.CONNECT);
        registerReceiver(myReciever,intentFilter);

        Intent i = new Intent(this,GPSProvider.class);
        startService(i);
    }

    private void enableButton(){
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                host = IP.getText().toString();
                port = Integer.parseInt(Port.getText().toString());
                Client c = new Client(getApplicationContext(),
                                    host,
                                    port);
                try{
                    b = c.execute().get();
                }catch (InterruptedException | ExecutionException e){
                    e.printStackTrace();
                }
            }
        });
    }
    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enableButton();
            }else {
                runtime_permissions();
            }
        }
    }

    private class MyReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            GPSData = (String) intent.getExtras().get("coordinates");
        }
    }
}
