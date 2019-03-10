package mygpstracker.android.mygpstracker.Sensors;

import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

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

    public String[] getNetworkInfo(){
        String[] info = new String[2];
        getActiveConnections();
        if(isWifiConn){
            info[0] = "WIFI";
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null) {
                info[1] = wifiInfo.getLinkSpeed() + " Mbps";
            }
        }
        else if(isMobileConn){
            info[0] = "MOBILE";
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo != null && networkInfo.isConnected())) {
                 info[1] = getMobileSpeed(networkInfo.getType(), networkInfo.getSubtype());
            }
        }
        else
            return null;


        return info;
    }


    public static String getMobileSpeed(int type, int subType) {
        // http://www.theappguruz.com/blog/android-cellular-network-info
        final int NETWORK_TYPE_EHRPD = 14; // Level 11
        final int NETWORK_TYPE_EVDO_B = 12; // Level 9
        final int NETWORK_TYPE_HSPAP = 15; // Level 13
        final int NETWORK_TYPE_IDEN = 11; // Level 8
        final int NETWORK_TYPE_LTE = 13; // Level 11
        if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "~ 50-100 kbps"; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "NETWORK TYPE CDMA (3G) Speed: 2 Mbps"; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "NETWORK TYPE EDGE (2.75G) Speed: 100-120 Kbps"; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "NETWORK TYPE EVDO_0"; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "NETWORK TYPE EVDO_A"; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "NETWORK TYPE GPRS (2.5G) Speed: 40-50 Kbps"; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "NETWORK TYPE HSDPA (4G) Speed: 2-14 Mbps"; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "NETWORK TYPE HSPA (4G) Speed: 0.7-1.7 Mbps"; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "NETWORK TYPE HSUPA (3G) Speed: 1-23 Mbps"; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "NETWORK TYPE UMTS (3G) Speed: 0.4-7 Mbps"; // ~ 400-7000 kbps
                // NOT AVAILABLE YET IN API LEVEL 7
                case NETWORK_TYPE_EHRPD:
                    return "NETWORK TYPE EHRPD"; // ~ 1-2 Mbps
                case NETWORK_TYPE_EVDO_B:
                    return "NETWORK_TYPE_EVDO_B"; // ~ 5 Mbps
                case NETWORK_TYPE_HSPAP:
                    return "NETWORK TYPE HSPA+ (4G) Speed: 10-20 Mbps"; // ~ 10-20 Mbps
                case NETWORK_TYPE_IDEN:
                    return "NETWORK TYPE IDEN"; // ~25 kbps
                case NETWORK_TYPE_LTE:
                    return "NETWORK TYPE LTE (4G) Speed: 10+ Mbps"; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "NETWORK TYPE UNKNOWN";
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

}
