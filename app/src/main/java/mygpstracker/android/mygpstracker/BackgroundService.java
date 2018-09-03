package mygpstracker.android.mygpstracker;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;



import java.text.DecimalFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
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
    private Timer timer;
    private TimerTask timerTask;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
        /*// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Thread t = new Thread(()->{
            Looper.prepare();
            runWithTask();
        });
        //stopSelf();
        t.setDaemon(true);
        t.start();

        return START_NOT_STICKY;
    }


    private void runWithTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                action();
            }
        };
        timer = new Timer(true);
        timer.schedule(timerTask,0, (long)(samplePolicy.getIntervalsInMinutes() * 60 /*minutes*/ * 1000  /*seconds*/));
    }


    private void action(){
        System.out.println("ACTION\n");
        int timesToTakeLocation = samplePolicy.getTimesToTakeLocation();
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<Location>[] futureLocations = new FutureTask[timesToTakeLocation];
        /* Using Future and ThreadPool running the requests of locations*/
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
        System.out.println("Done Looping\n");
        double[] meanLocation = gpsLocation.getMeanLocation(locations);
        Date currentTime = Calendar.getInstance().getTime();
        String[] dateArr = currentTime.toString().split(" ");
        String timeAndDate = dateArr[1] + " " + dateArr[2] + " " + dateArr[5] + " " + dateArr[3]; //Jul 23 2018 20:42:29
        //Log.getInstance().write(timeAndDate + ": LAT = " + df6.format(meanLocation[0]) + ", LON = " + df6.format(meanLocation[1]) + "\n");
        Log.getInstance().writeWithResolver(timeAndDate, df6.format((meanLocation[0])), df6.format(meanLocation[1]));
    }



    @Override
    public void onDestroy(){
        // I want to restart this service again in one given time
        System.out.println("BackGroundService - onDestroy");
        timerTask.cancel();
        timer.purge();
        timer.cancel();
        super.onDestroy();

/*        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(samplePolicy.getIntervalsInMinutes() * 60 *//*minutes*//* * 1000  *//*seconds*//*),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );*/

    }

/*    private int getMonthNumber(String month){
        switch(month){
            case "Jan":
            case "Feb":
            case "Mar":
            case "Apr":
            case "May":
            case "Jun":
                case

        }
    }*/

}
