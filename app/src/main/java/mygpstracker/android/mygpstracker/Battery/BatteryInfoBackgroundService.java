package mygpstracker.android.mygpstracker.Battery;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import mygpstracker.android.mygpstracker.DB.SqliteHelper;


public class BatteryInfoBackgroundService extends Service {

    private double samplingTime = 0.25;
    public static AtomicBoolean isRunning = new AtomicBoolean(false);
    private MyBatteryInfo myBatteryInfo;
    private Timer timer;
    private TimerTask timerTask;
    private SqliteHelper sqliteHelper;

    IBinder mBinder = new BatteryInfoBackgroundService.LocalBinder();

    public class LocalBinder extends Binder {
        public BatteryInfoBackgroundService getServerInstance() {
            return BatteryInfoBackgroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sqliteHelper = new SqliteHelper(getApplicationContext());
        myBatteryInfo = new MyBatteryInfo(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        super.onStartCommand(intent, flags, startId);
        requestBatteryUpdatesButtonHandler();
        Toast.makeText(this, "Battery Service Started with period: " + samplingTime, Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    private void requestBatteryUpdatesButtonHandler() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Map<String,String> data = myBatteryInfo.getData();
                BatteryInfoWrapper batteryInfoWrapper = new BatteryInfoWrapper(data);
                sqliteHelper.createBatteryInfo(batteryInfoWrapper);
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask,0, (long)(samplingTime * 60 /*minutes*/ * 1000  /*seconds*/));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent(this, BatteryBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        Toast.makeText(this, "Battery Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
