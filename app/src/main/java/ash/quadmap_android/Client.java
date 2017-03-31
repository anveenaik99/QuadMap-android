package ash.quadmap_android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.security.AccessController.getContext;

public class Client extends AsyncTask<Void,Void,BufferedWriter>{

    private OutputStreamWriter os;
    private String host;
    private int soc;
    private PrintWriter out ;
    public  Context c;
    Client(Context _c, String _host, int _socket){
        c = _c;
        host = _host;
        soc = _socket;
    }
    @Override
    protected BufferedWriter doInBackground(Void... voids) {

        Socket client;
        try {
            client = new Socket(host, soc);
            os = new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8);
            out = new PrintWriter(client.getOutputStream(), true);
            Log.d("Client", "Successfully Executed");
        } catch (IOException ignored) {
            Log.e("ClientError","Can't Connect");
        }
        BufferedWriter bw = new BufferedWriter(out);
        try {
            bw.write("Connected");
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bw;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(BufferedWriter bufferedWriter) {
        super.onPostExecute(bufferedWriter);
    }
}