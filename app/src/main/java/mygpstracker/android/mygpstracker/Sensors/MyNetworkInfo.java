package mygpstracker.android.mygpstracker.Sensors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by doroy on 09-Oct-18.
 */

public class MyNetworkInfo {

    private static final String TAG = "MyNetworkInfo";

    private ConnectivityManager connectivityManager;
    boolean isWifiConn = false;
    boolean isMobileConn = false;

    public MyNetworkInfo(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void getActiveConnections(){
        isWifiConn = false;
        isMobileConn = false;
        for (Network network : connectivityManager.getAllNetworks()) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                isWifiConn |= networkInfo.isConnected();
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                isMobileConn |= networkInfo.isConnected();
            }
        }
/*        Log.d(TAG, "Wifi connected: " + isWifiConn);
        Log.d(TAG, "Mobile connected: " + isMobileConn);*/
    }

    public boolean isOnline() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public String getNetworkDataString(){
        getActiveConnections();
        String data = "\nNetwork Information:\n";
        data += "\t\tWifi: " + isWifiConn + "\n";
        data += "\t\tMobile: " + isMobileConn + "\n";
        data += "\t\tOnline: " + isOnline() + "\n";
        return data;
    }
}
