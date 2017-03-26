package ash.quadmap_android;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client extends Service{

    private OutputStreamWriter os;
    private String host;
    private int soc;
    private PrintWriter out ;
    public  Context c;
    BufferedWriter bw;
    private final IBinder mBinder = new MyBinder();
    Client(Context _c, String _host, int _socket){
        c = _c;
        host = _host;
        soc = _socket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bw = null;
        try {
            Socket client = new Socket(host, soc);
            os = new OutputStreamWriter(
                    client.getOutputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(client.getOutputStream(), true);
            bw = new BufferedWriter(out);
            Log.d("Client", "Successfully Executed");
        }catch (IOException ignored) {

        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder{
        Client getService(){
            return Client.this;
        }
    }

    public BufferedWriter getWriter(){
        return bw;
    }
}