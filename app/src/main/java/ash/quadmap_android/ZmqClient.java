package ash.quadmap_android;
import android.os.AsyncTask;
import android.os.Handler;

import org.zeromq.ZMQ;

class ZmqClient extends AsyncTask<String, Void, String> {

    ZmqClient() {
    }

    @Override
    protected String doInBackground(String... params) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket socket = context.socket(ZMQ.REQ);

        socket.connect("tcp://"+params[0]+":"+params[1]);

        socket.send(params[2].getBytes(), 0);
        String result = new String(socket.recv(0));

        socket.close();
        context.term();

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
    }
}