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
import android.widget.TextView;

public class ConnectScreen extends AppCompatActivity {

    MyReciever myReciever;
    private Button connect;
    TextView port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_screen);

        connect = (Button)findViewById(R.id.connect_btn);
        port = (TextView) findViewById(R.id.Port_text);
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
            String coordinates = (String) intent.getExtras().get("coordinates");
            port.setText(coordinates);
        }
    }
}
