package mygpstracker.android.mygpstracker.Sensors;

import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.format.Formatter;
import android.util.Log;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;

/**
 * Created by doroy on 09-Oct-18.
 */

public class MyNetworkInfo {

    private static final String TAG = "MyNetworkInfo";

    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    UsageStatsManager usageStatsManager;
    NetworkStatsManager networkStatsManager;
    private boolean isWifiConn = false;
    private boolean isMobileConn = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public MyNetworkInfo(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

    }

    private String getWifiInformation(){

        if(wifiManager != null){
            String ans = "";
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null && isWifiConn) {
                ans += "\t\tIP: " + Formatter.formatIpAddress(wifiInfo.getIpAddress()) + "\n";
                ans += "\t\tMAC: " + wifiInfo.getMacAddress() + "\n";
                ans += "\t\tFrequency: " + wifiInfo.getFrequency() + " MHz\n";
                ans += "\t\tLinkSpeed: " + wifiInfo.getLinkSpeed() + " Mbps\n";
                return ans;
            }

        }
        return null;
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
        String wifiInfo;
        if((wifiInfo = getWifiInformation()) != null){
            data += wifiInfo;
        }

        return data;
    }

}
