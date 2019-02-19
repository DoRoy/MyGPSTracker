package ClientPackage;

import android.content.Context;
import java.io.*;
import mygpstracker.android.mygpstracker.DB.SqliteHelper;

public class SendCSVClientStrategy implements IClientStrategy {

    private SqliteHelper sqliteHelper;
    public String tableName = "Y";
    public String userName = "X";

    public SendCSVClientStrategy(Context context){
        sqliteHelper = new SqliteHelper(context);
    }

    public SendCSVClientStrategy(Context context, String tableName, String userName){
        this(context);
        this.tableName = tableName;
        this.userName = userName;
    }

    /**
     * The function that handles the communication with the server.
     * @param inFromServer  Server Input Stream
     * @param outToServer   Server Output Stream
     */
    @Override
    public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
        ObjectOutputStream toServer = null;
        try {
            // Wrap the OutputStream with Object Stream to send Objects
            toServer = new ObjectOutputStream(outToServer);
            toServer.flush();

            // Send the Name of the user and the table being sent to the server to organize the files
            toServer.writeObject(userName + "_" + tableName);
            toServer.flush();

            // Get the table content as a CSV formatted String
            String content = sqliteHelper.getCSVString(tableName);

            if (content == null){ //problem with table or the table is empty. we want to notify the server that an error has accord
                String errorMsg = "A problem has accord with user " + userName + " on table " + tableName;
                toServer.writeObject(errorMsg);
                System.out.println("Done - File was sent.");

            }else { // no problem, we send the table data and delete it.
                byte[] byteArrayToSend = content.getBytes();
                System.out.println("Sending " + "FileToSend" + "(" + byteArrayToSend.length + " bytes)");
                toServer.writeObject(byteArrayToSend);
                System.out.println("Done - File was sent.");
            }
            toServer.flush();
            toServer.close();
            sqliteHelper.resetTable(tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            if (toServer != null) {
                try {
                    toServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}
