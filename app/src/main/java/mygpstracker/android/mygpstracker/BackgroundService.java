package mygpstracker.android.mygpstracker;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;



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

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Location[] locations = new Location[timesToTakeLocation];
                for(int i = 0; i < timesToTakeLocation; i++){
                    gpsLocation.getLocation();
                    locations[i] = gpsLocation.getLastLocation();
                    try {
                        wait(6000/timesToTakeLocation);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                double[] meanLocation = gpsLocation.getMeanLocation(locations);
                Date currentTime = Calendar.getInstance().getTime();
                Log.getInstance().write(currentTime + ": Lantitude - " + meanLocation[0] + ", Longtitude - " + meanLocation[1] + "\n");

            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask,0, (int)(intervalsInMinutes * 60 /*minutes*/ * 1000 /*seconds*/));
        return super.onStartCommand(intent,flags,startID);
    }



}
