package mygpstracker.android.mygpstracker.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import mygpstracker.android.mygpstracker.MainActivity;
import mygpstracker.android.mygpstracker.SamplePolicy;

/**
 * Created by doroy on 29-Aug-18.
 */

public abstract class ASensorMeasures implements ISensor {

    public static SensorManager sensorManager;

    protected String sensorName;

    protected Sensor sensor;
    protected SensorEventListener sensorEventListener;

    public static final String MAX_VALUE = "max_value";
    public static final String MIN_VALUE = "min_value";
    public static final String MEAN_VALUE = "mean_value";
    public static final String MEDIAN_VALUE = "median_value";

    protected AtomicInteger atomicInteger = new AtomicInteger(-1);
    protected /*static*/ Semaphore semaphore = new Semaphore(0);
    protected /*static*/ Semaphore semaphore2 = new Semaphore(0);
    protected /*static*/ volatile boolean lock = false;

    protected String TAG;

    public ASensorMeasures(String sensorName) {
        this.sensorName = sensorName + "_";
    }

    public ASensorMeasures(Sensor sensor){
        this.sensor = sensor;
        this.sensorName = sensor.getName() + "_";
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                Date date = new Date(event.timestamp);
                int counter = atomicInteger.getAndIncrement();

                int size = SamplePolicy.getTimesToTakeLocation();

                if(specificCases()){
                    size = 1;
                }

                if(counter <= 0) {
                    if (lock){
                        try {
                            Log.d(TAG, "OnSensorChanged - Listener - Acquiring semaphore - " + semaphore.availablePermits());
                            semaphore.acquire();
                            Log.d(TAG, "OnSensorChanged - Listener - semaphore acquired - " + semaphore.availablePermits());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(counter < 0){
                        counter = atomicInteger.getAndIncrement();
                    }
                    initialValues(size);

                }
                Log.d(TAG, "OnSensorChanged - Listener - " + counter + " received information at " + date + " " + sensorName);

                setValues(event, counter);

                if (counter >= size - 1) {
                    atomicInteger.set(0);
                    sensorManager.unregisterListener(this);
                    lock = true;
                    semaphore2.release();

                    Log.d(TAG, "OnSensorChanged - Listener - semaphore2 released - " + semaphore.availablePermits());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Log.d(TAG, "onAccuracyChanged - Listener");
            }
        };
    }

    private boolean specificCases(){
        int type = sensor.getType();
        if(type == Sensor.TYPE_PROXIMITY){
            // spcial case where it wont update the same time and need to change the size to 1
            return true;
        }
        return false;
    }

    public abstract void initialValues(int size);

    public abstract void setValues(SensorEvent event, int index);

    @Override
    public Map<String, Double> getData() {
        final Map<String, Double>[] map = new Map[]{new Hashtable<>(4 /*Parameters*/ * 3 /*Axis*/)};
        Log.d(TAG, "getData - Entered getData()");
        Thread t = new Thread(() -> {
            map[0] = threadRun();
        });
        t.setDaemon(true);
        t.start();
        Log.d(TAG, "getData - started t thread");
        try {
            t.join();
            Log.d(TAG, "getData - After t.join()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getData - returned DATA");
        return map[0];
    }

    private Map<String,Double> threadRun(){
        Thread tt = new Thread(() -> sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL));
        tt.setDaemon(true);
        tt.start();
        try {
            tt.join();
            Log.d(TAG, "threadRun - inside t - after tt.join()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Double> map = new Hashtable<>(4 /*Parameters*/ * 3 /*Axis*/);

        Log.d(TAG, "threadRun - inside t -  registered listener");

        try {
            Log.d(TAG, "threadRun - inside t -  before acquiring semaphore2 - " + semaphore.availablePermits());
            semaphore2.acquire();
            Log.d(TAG, "threadRun - inside t -  Acquired semaphore2 - " + semaphore.availablePermits());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        map.putAll(getStatisticsOfData());

        semaphore.release();
        Log.d(TAG, "threadRun - inside t - released semaphore - " + semaphore.availablePermits());
        return map;
    }

    protected abstract Map<String, Double> getStatisticsOfData();


    protected double getMax(@NonNull float[] data){
        double ans = Double.MIN_VALUE;
        for(int i = 0; i < data.length; i++){
            ans = Math.max(ans,data[i]);
        }
        return ans;
    }

    protected double getMin(@NonNull float[] data){
        double ans = Double.MAX_VALUE;
        for(int i = 0; i < data.length; i++){
            ans = Math.min(ans,data[i]);
        }
        return ans;
    }

    protected double getMedian(@NonNull float[] data){
        // First we sort the array
        Arrays.sort(data);
        int n = data.length;
        // check for even case
        if (n % 2 != 0)
            return (double)data[n / 2];

        return (double)(data[(n - 1) / 2] + data[n / 2]) / 2.0;
    }

    protected double getMean(@NonNull float[] data){
        double sum = 0;
        for(int i = 0; i < data.length; i ++){
            sum += data[i];
        }
        return sum / data.length;
    }

}
