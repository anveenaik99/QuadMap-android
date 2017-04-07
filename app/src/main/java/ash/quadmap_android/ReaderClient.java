package ash.quadmap_android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ReaderClient extends AsyncTask<Void,Void,BufferedReader> {

    private String host;
    private int soc;
    public Context c;
    Socket client;
    BufferedReader in;

    ReaderClient(Context _c, String _host, int _socket){
        c = _c;
        host = _host;
        soc = _socket;
    }
    @Override
    protected BufferedReader doInBackground(Void... voids) {
        try {
            client = new Socket(host, soc);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // out.println("Chal gaaya");
            // out.flush();
            //bw = new BufferedWriter(out);
            Log.d("Client", "Successfully Excuted");
        }catch (IOException ignored) {

        }
        return in;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}