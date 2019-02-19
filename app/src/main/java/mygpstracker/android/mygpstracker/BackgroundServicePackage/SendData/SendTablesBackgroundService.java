package mygpstracker.android.mygpstracker.BackgroundServicePackage.SendData;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import ClientPackage.Client;
import ClientPackage.SendCSVClientStrategy;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.ABackgroundService;
import mygpstracker.android.mygpstracker.Battery.BatteryInfoWrapper;


public class SendTablesBackgroundService extends ABackgroundService {

    private Client[] clients;


    @Override
    public void onCreate() {

        setDelay(getDelayToOne());
        setPeriod(getDoubleInHours(24));

        String[] tablesNames = sqliteHelper.getAllTablesNames();
        clients = new Client[tablesNames.length];
        for(int i = 0; i < tablesNames.length; i++){
            try {
                clients[i] = new Client(InetAddress.getByName("10.100.102.6"),5400, new SendCSVClientStrategy(getApplicationContext(), tablesNames[i], "X"));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        super.onCreate();
    }

/*    @SuppressLint("NewApi")
    private double getDelayToOne(){
        double millisecondsDelay = 0;
        LocalDateTime localDateTime = LocalDateTime.now();

        millisecondsDelay += getDoubleInHours(25 - localDateTime.getHour());
        millisecondsDelay += getDoubleInMinutes(60 - localDateTime.getMinute());

        return millisecondsDelay;
    }*/

    private double getDelayToOne(){
        return getDoubleInSeconds(5);
    }



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
