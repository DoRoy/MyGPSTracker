package ClientPackage;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A Class that communicates with the server.
 * The class is implemented as 'Strategy Pattern'
 */

public class Client  extends AsyncTask<String, Void, Boolean> {

    private InetAddress serverIP;
    private int serverPort;
    private IClientStrategy clientStrategy;

    /**
     * Class Constructor
     * @param serverIP       The Server IP address
     * @param serverPort     The Server listening port
     * @param clientStrategy    An instance of a Class that implements 'IClientStrategy'.
     *                          This class handles the communication with the server.
     */
    public Client(InetAddress serverIP, int serverPort, IClientStrategy clientStrategy) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clientStrategy = clientStrategy;
    }


    private void communicateWithServer() {
        Socket theServer = null;
        try {
            System.out.println("Trying to Created Socket");
            // Create the Socket with the server
            theServer = new Socket(serverIP, serverPort);

            System.out.println(String.format("Client: Client is connected to server (IP: %s, port: %s)", serverIP, serverPort));
            System.out.println(String.format("Client: Client Details (Remote: %s, InetAddress: %s, SocketAddress: %s)", theServer.getRemoteSocketAddress(), theServer.getInetAddress(), theServer.getLocalSocketAddress().toString()));

            // Call to the handling function
            clientStrategy.clientStrategy(theServer.getInputStream(), theServer.getOutputStream());
            theServer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // close all the streams and the server socket
            if (theServer != null){
                try {
                    if (theServer!= null && !theServer.isClosed()) theServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


/*****************************   AsyncTask ***************************/


    @Override
    protected Boolean doInBackground(String... strings) {
        try{
            communicateWithServer();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
