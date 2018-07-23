package mygpstracker.android.mygpstracker;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


/**
 * Responsible to run at the background all the time, even if the app isn't on.
 */

public class BackgroundService extends Service {

    TimerTask timerTask;
    Timer timer;
    public static ILocation gpsLocation;
    public static int timesToTakeLocation;
    public static double intervalsInMinutes;

/*    public BackgroundService(Application app, int timeToTakeLocation, int intervalsInMinutes) {
        this.app = app;
        this.timesToTakeLocation = timeToTakeLocation;
        this.intervalsInMinutes = intervalsInMinutes;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        /*// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
    }

    public int onStartCommand(Intent intent, int flags, int startID){
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Future<Location>[] futureLocations = new FutureTask[timesToTakeLocation];
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
        //Location location = gpsLocation.getLastKnownLocation();
        Date currentTime = Calendar.getInstance().getTime();
/*        if(!Log.getInstance().write(currentTime + ": Lantitude - " + location.getLatitude()+ ", Longtitude - " + location.getLongitude() + "\n\n"))
            System.out.println("\nDidn't print to log\n");
        System.out.println(currentTime + ": Lantitude - " + location.getLatitude()+ ", Longtitude - " + location.getLongitude() + "\n\n");*/
        Log.getInstance().write(currentTime + ": Lantitude - " + meanLocation[0] + ", Longtitude - " + meanLocation[1] + "\n\n");
        System.out.println(currentTime + ": Lantitude - " + meanLocation[0] + ", Longtitude - " + meanLocation[1] + "\n");
        stopSelf();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(intervalsInMinutes * 60 /*minutes*/ * 1000  /*seconds*/),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );
    }


/*    public int onStartCommand(Intent intent, int flags, int startID){

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Location[] locations = new Location[timesToTakeLocation];
                for(int i = 0; i < timesToTakeLocation; i++){
                    locations[i] = gpsLocation.getLastKnownLocation();
                    try {
                        wait(6000/timesToTakeLocation);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                double[] meanLocation = gpsLocation.getMeanLocation(locations);
                Date currentTime = Calendar.getInstance().getTime();
                //Log.getInstance().write(currentTime + ": Lantitude - " + meanLocation[0] + ", Longtitude - " + meanLocation[1] + "\n");
                System.out.println(currentTime + ": Lantitude - " + meanLocation[0] + ", Longtitude - " + meanLocation[1] + "\n");

            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask,0, (int)(intervalsInMinutes * 60 *//*minutes*//* * 1000 *//*seconds*//*));
        return super.onStartCommand(intent,flags,startID);
    }*/



}
