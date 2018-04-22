import java.net.*;
import java.io.*;
import java.util.*;

public class ProxyThread extends Thread {

    private Socket socket;
    private static final int BUFFER_SIZE = 32768;

    public ProxyThread(Socket socket) {
        super("ProxyThread");
        this.socket = socket;
    }

    public void run() {
        
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
    
            boolean first_line_reached = false;
            String server_url = "";

            while ((inputLine = in.readLine()) != null) {
                try {
                    StringTokenizer tok = new StringTokenizer(inputLine);
                    tok.nextToken();
                } catch (Exception e) {
                    break;
                }
                if (!first_line_reached) {
                    String[] tokens = inputLine.split(" ");
                    if (tokens.length > 0 )
                        server_url = tokens[1];
                    else {
                        return;
                    }
                    System.out.println("--------------------------");
                    for(int i = 0; i < tokens.length; i++)
                        System.out.println(tokens[i]);
                    break;
                }
            }


            BufferedReader rd = null;
            try {
                System.out.println("Making connection to : " + server_url);

                URL url = new URL(server_url);
                URLConnection conn = url.openConnection();

                conn.setDoInput(true);

                InputStream is = null;
                HttpURLConnection huc = (HttpURLConnection)conn;
                if (conn.getContentLength() > 0) {
                    try {
                        is = conn.getInputStream();
                        rd = new BufferedReader(new InputStreamReader(is));
                    } catch (IOException ioe) {
                        socket.close();
                    }
                }
                else{
                    socket.close();
                }

                byte by[] = new byte[ BUFFER_SIZE ];
                if (is != null) {
                    int index = is.read(by, 0, BUFFER_SIZE);
                    while (index != -1) {
                        out.write(by, 0, index);
                        index = is.read(by, 0, BUFFER_SIZE);
                    }
                    out.flush();
                }

            } catch (Exception e) {
                out.writeBytes("");
            }

            if (rd != null) {
                rd.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isHTTP (String url){
        
        if (url.substring(7).equals("http://"))
            return true;
        else{
            if (url.substring(8).equals("https://"))
                return false;
            else{
                if (url.split(":")[1].equals("443"))
                    return false;
                else
                    return true;
            }
        }
        
    }

}