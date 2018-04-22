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
            System.exit(-1);
        }
        String[] URLs = new String[10000];
        int index = 0;
            try {
                Socket socket;
                while ((socket = ServerSocket.accept()) != null) {

//                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String URLtoCall = "";
//                String inputLine = in.readLine();
//                if ( inputLine != null){
//                    String[] tokens = inputLine.split(" ");
//                    URLtoCall = tokens[1];
//                }
//                if (Arrays.asList(URLs).contains(URLtoCall)) {
//                    System.out.println("URL exists!");
//                    new ProxyResponseThread(socket, URLtoCall).start();
//                    continue;
//                }
//                URLs[index] = URLtoCall;
//                index++;
//                SocketAddress address =socket.getRemoteSocketAddress();
//                String url=((InetSocketAddress)address).getHostString();
                    new ProxyThread(socket).start();
//                      new ProxyThread(socket).start();
//
//                new ProxyResponseThread(socket, url).start();
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
