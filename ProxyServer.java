import android.os.AsyncTask;

import java.net.*;
import java.io.*;

public class ProxyServer extends AsyncTask<Void, Void, Void> {

    ServerSocket ServerSocket;
    @Override
    protected Void doInBackground(Void... voids) {
        boolean listening = true;

        int port = 8080;	// our port to listen


        try {
            ServerSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            return null;
        }

            try {
                Socket socket;
                // Start listening to our port
                while ((socket = ServerSocket.accept()) != null) {

                    new ProxyThread(socket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();

        }
        try {
            ServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }
}
