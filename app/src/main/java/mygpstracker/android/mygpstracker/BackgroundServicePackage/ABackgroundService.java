package mygpstracker.android.mygpstracker.BackgroundServicePackage;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.DB.SqliteHelper;


/**
 * The abstract class that summarize most uses for collection and sending data
 * Each class tha will inherit this class is suppose to work similar to this.
 */
public abstract class ABackgroundService extends Service {

    protected String TAG = getClassChild().getName();

    private double period = getDoubleInMinutes(0.25);

    private double delay = getDoubleInMinutes(0.25);
    private Timer timer;
    protected static SqliteHelper sqliteHelper;

    IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ABackgroundService getServerInstance() {
            return ABackgroundService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        sqliteHelper = new SqliteHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        super.onStartCommand(intent, flags, startId);

        // Start the background task
        startTimerTask();

        Log.d(TAG, "Service Started with period: " + getTimeDescriptionForSampling());
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent(this, getClassChild());
        if (timer != null){
            timer.cancel();
            timer = null;
        }
        Log.d( TAG, "Service Destroyed");
    }




    /**
     * Starts the TimerTask
     */
    protected void startTimerTask(){
        List<TimerTask> timerTaskList = getTimerTask();
        timer = new Timer(true);
        for (TimerTask task: timerTaskList) {
            timer.scheduleAtFixedRate(task,(long) delay, (long) period);
        }

    }

    /************************* Abstract Methods *****************************/


    /**
     * Child should implement this as:
     * return this.getClass();
     * @return  this.getClass();
     */
    protected abstract Class<? extends ABackgroundService> getClassChild();

    /**
     * This method needs to create a new Timer Task and return it.
     * @return  A new TimerTask with the working logic.
     */
    protected abstract List<TimerTask> getTimerTask();


    /************************* Util Methods *****************************/
    /**
     * Convert the period to seconds
     * @return
     */
    protected double getDoubleInSeconds(double seconds){
        return (long)(seconds * 1000  /*seconds*/);
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    /**
     * Convert the period to minutes
     * @return
     */
    protected double getDoubleInMinutes(double minutes){
        return (long)(minutes *  60 /*minutes*/ * 1000  /*seconds*/ );
    }

    /**
     * Convert the period to hours
     * @return
     */
    protected double getDoubleInHours(double hours){
        return (long)(hours * 60 /*hours*/ *  60 /*minutes*/ * 1000  /*seconds*/ );
    }

    protected String getTimeDescriptionForSampling(){
        String ans = "";
        if (period < 1000 * 60)
            ans = String.valueOf(period / 1000 ) + " Seconds";
        else if (1000 * 60 <= period && period < 1000 * 60 * 60)
            ans = String.valueOf(period / 1000 / 60) + " Minutes";
        else if (1000 * 60 * 60 <= period && period < 1000 * 60 * 60 * 24)
            ans = String.valueOf(period / 1000 / 60 / 60 ) + " Hours";
        else if (1000 * 60 * 60 * 24 <= period)
            ans = String.valueOf(period / 1000 / 60 / 60 / 24) + " Days";

        return ans;
    }

}

