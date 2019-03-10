package mygpstracker.android.mygpstracker.BackgroundServicePackage.SendData;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import ClientPackage.Client;
import ClientPackage.SendCSVClientStrategy;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.ABackgroundService;


/**
 * Sends the Data collected to the Server
 */
public class SendTablesBackgroundService extends ABackgroundService {

    private Client[] clients; // Different clients so they will be independent.
    private String mPhoneNumber; // the phone number

    @Override
    public void onCreate() {
        // Set the delays
        setDelay(getDelayToOne());
        setPeriod(getDoubleInHours(24));
        // get the telephone number from the system.
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mPhoneNumber = tMgr.getLine1Number();

        // get the tables
        String[] tablesNames = sqliteHelper.getAllTablesNames();
        clients = new Client[tablesNames.length];
        for(int i = 0; i < tablesNames.length; i++){
            try {
                // initialize all the clients
                clients[i] = new Client(InetAddress.getByName("10.100.102.6"),5400, new SendCSVClientStrategy(getApplicationContext(), tablesNames[i], mPhoneNumber));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private double getDelayToOne(){
        double millisecondsDelay = 0;
        LocalDateTime localDateTime = LocalDateTime.now();

        millisecondsDelay += getDoubleInHours(25 - localDateTime.getHour());
        millisecondsDelay += getDoubleInMinutes(60 - localDateTime.getMinute());

        return millisecondsDelay;
    }

//    private double getDelayToOne(){
//        return getDoubleInSeconds(5);
//    }



    @Override
    protected Class<? extends ABackgroundService> getClassChild() {
        return this.getClass();
    }

    @Override
    protected List<TimerTask> getTimerTask() {
        ArrayList<TimerTask> list = new ArrayList<>();
        list.add(new TimerTask() {
            @Override
            public void run() {
                for(Client client : clients)
                    client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        return list;
    }
}
