package mygpstracker.android.mygpstracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

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
    public static int timesToTakeLocation;
    public static double intervalsInMinutes;
    private static DecimalFormat df7 = new DecimalFormat(".0000000");


    @Override
    public IBinder onBind(Intent intent) {
        return null;
        /*// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Future<Location>[] futureLocations = new FutureTask[timesToTakeLocation];
        /* Using Future and TheadPool running the requests of locations*/
        for(int i = 0; i < timesToTakeLocation; i++){
            futureLocations[i] = pool.submit(new Callable<Location>() {
                @Override
                public Location call() throws Exception {
                    return gpsLocation.getLastKnownLocation();
                }
            });
            try {
                Thread.sleep(6000/timesToTakeLocation);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Location[] locations = new Location[timesToTakeLocation];
        for(int i = 0; i < timesToTakeLocation; i++){
            try {
                locations[i] = futureLocations[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        double[] meanLocation = gpsLocation.getMeanLocation(locations);
        Date currentTime = Calendar.getInstance().getTime();
        String[] dateArr = currentTime.toString().split(" ");
        String timeAndDate = dateArr[1] + " " + dateArr[2] + " " + dateArr[5] + " " + dateArr[3]; //Jul 23 2018 20:42:29
        Log.getInstance().write(timeAndDate + ": LAT - " + df7.format(meanLocation[0]) + ", LON - " + df7.format(meanLocation[1]) + "\n");
        //System.out.println(timeAndDate + ": LAT - " + df7.format(meanLocation[0]) + ", LON - " + df7.format(meanLocation[1]) + "\n");
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        // I want to restart this service again in one given time
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(intervalsInMinutes * 60 /*minutes*/ * 1000  /*seconds*/),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );
    }


}
