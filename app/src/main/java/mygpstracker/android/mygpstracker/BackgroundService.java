package mygpstracker.android.mygpstracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


/**
 * Responsible to run at the background all the time, even if the app isn't on.
 */

public class BackgroundService extends Service {

    public static ILocation gpsLocation; // allows to change the strategy of the sensors
    public static SamplePolicy samplePolicy;
    private static DecimalFormat df6 = new DecimalFormat(".000000");


    @Override
    public IBinder onBind(Intent intent) {
        return null;
        /*// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Thread t = new Thread(()->{
            action();
            restartService();
        });
        //stopSelf();
        t.start();

        return START_NOT_STICKY;
    }

    private void restartService() {
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(samplePolicy.getIntervalsInMinutes() * 60 /*minutes*/ * 1000  /*seconds*/),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );
    }

    private void action(){
        System.out.println("ACTION\n");
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<Location>[] futureLocations = new FutureTask[samplePolicy.getTimesToTakeLocation()];
        /* Using Future and ThreadPool running the requests of locations*/
        for(int i = 0; i < samplePolicy.getTimesToTakeLocation(); i++){
            futureLocations[i] = pool.submit(new Callable<Location>() {
                @Override
                public Location call() throws Exception {
                    return gpsLocation.getLastKnownLocation();
                }
            });
            try {
                Thread.sleep(6000/samplePolicy.getTimesToTakeLocation());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done Looping1\n");
        Location[] locations = new Location[samplePolicy.getTimesToTakeLocation()];
        for(int i = 0; i < samplePolicy.getTimesToTakeLocation(); i++){
            try {
                locations[i] = futureLocations[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done Looping2\n");
        double[] meanLocation = gpsLocation.getMeanLocation(locations);
        Date currentTime = Calendar.getInstance().getTime();
        String[] dateArr = currentTime.toString().split(" ");
        String timeAndDate = dateArr[1] + " " + dateArr[2] + " " + dateArr[5] + " " + dateArr[3]; //Jul 23 2018 20:42:29
        Log.getInstance().write(timeAndDate + ": LAT - " + df6.format(meanLocation[0]) + ", LON - " + df6.format(meanLocation[1]) + "\n");

    }


    @Override
    public void onDestroy(){
        // I want to restart this service again in one given time
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(samplePolicy.getIntervalsInMinutes() * 60 /*minutes*/ * 1000  /*seconds*/),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
/*        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);*/
        restartService();
        super.onTaskRemoved(rootIntent);
    }
}
