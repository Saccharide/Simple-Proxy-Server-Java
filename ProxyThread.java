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

    /*****************************************************/
    /* A simple proxy server needs to do the following:  */
    /*     1) Get     request  from client               */
    /*     2) Forward request  to   server               */
    /*     3) Get     response from server               */
    /*     4) Forward response to   client               */
    /*****************************************************/
    public void run() {

        try {
            // Setting the input stream and output stream for future analysis.
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Storing client request
            String inputLine;

            // Setting a flag to see if we are on the first line
            boolean first_line_reached = false;
            
            // Initialize the url we will get from client socket
            String server_url = "";

            /*******************************/
            /* Step 1                      */
            /* Getting request from client */
            /*******************************/
            while ((inputLine = in.readLine()) != null) {
                try {
                    StringTokenizer tok = new StringTokenizer(inputLine);
                    tok.nextToken();
                } catch (Exception e) {
                    break;
                }
                // Analyze the first line of the network request from socket to find the url and protocol
                if (!first_line_reached) {
                    String[] tokens = inputLine.split(" ");
                    if (tokens.length > 0 )
                        server_url = tokens[1];
                    else {
//                        socket.close();
                        return;
                    }
                    System.out.println("-----------------------------------------------");
                    for(int i = 0; i < tokens.length; i++)
                        System.out.println(tokens[i]);

                    break;
                }
            }
            /* Step 1 complete                    */
            /* Finish getting request from client */
            
            BufferedReader rd = null;
            try {
                System.out.println("Making connection to : " + server_url);

                /***************************/
                /* Step 2                  */
                /* Send request to server  */
                /***************************/
                
                URL url = new URL(server_url);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                /* Step 2 complete                    */
                /* Finish sending request to server   */

                /*****************************/
                /* Step 3                    */
                /* Get response from server  */
                /*****************************/
                
                InputStream is = null;
                
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
                /* Step 3 complete                     */
                /* Finish getting response from server */


                /***************************/
                /* Step 4                  */
                /* Send response to client */
                /***************************/
                
                byte by[] = new byte[ BUFFER_SIZE ];
                if (is != null) {
                    int index = is.read(by, 0, BUFFER_SIZE);
                    while (index != -1) {
                        out.write(by, 0, index);
                        index = is.read(by, 0, BUFFER_SIZE);
                    }
                    out.flush();
                }
                
                /* Step 4 complete                     */
                /* Finish sending response to client   */

            } catch (Exception e) {
                //can redirect this to error log
                System.err.println("Encountered exception: " + e);
                //encountered error - just send nothing back, so
                //processing can continue
                out.writeBytes("");
            }

            //close all resources
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