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

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by doroy on 29-Aug-18.
 */

public abstract class ASensorMeasures implements ISensor {

    public static SensorManager sensorManager ;

    protected String sensorName;

    protected Sensor sensor;
    protected SensorEventListener sensorEventListener;

    public static final String MAX_VALUE = "max_value";
    public static final String MIN_VALUE = "min_value";
    public static final String MEAN_VALUE = "mean_value";
    public static final String MEDIAN_VALUE = "median_value";
    public static final String DEVIATION_VALUE = "deviation_value";
    public static final String POWER_VALUE = "power_value";
    public static final String MAX_RANGE_VALUE = "maxRange_value";

    protected AtomicInteger atomicInteger = new AtomicInteger(-1);
    protected /*static*/ Semaphore semaphore = new Semaphore(0);
    protected /*static*/ Semaphore semaphore2 = new Semaphore(0);
    protected /*static*/ volatile boolean lock = false;

    protected String TAG;

/*    public ASensorMeasures(String sensorName) {
        this.sensorName = sensorName + "_";
    }*/

    public ASensorMeasures(Sensor sensor){
        this.sensor = sensor;
        this.sensorName = sensor.getName();
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
                            //Log.d(TAG, "OnSensorChanged - Listener - Acquiring semaphore - " + semaphore.availablePermits());
                            semaphore.acquire();
                            //Log.d(TAG, "OnSensorChanged - Listener - semaphore acquired - " + semaphore.availablePermits());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if(counter < 0){
                        counter = atomicInteger.getAndIncrement();
                    }
                    //initialValues(size);
                    initialValues(size, event.values.length);

                }
                //Log.d(TAG, "OnSensorChanged - Listener - " + counter + " received information at " + date + " " + sensorName);

                setValues(event, counter);

                if (counter >= size - 1) {
                    atomicInteger.set(0);
                    sensorManager.unregisterListener(this);
                    lock = true;
                    semaphore2.release();

                    //Log.d(TAG, "OnSensorChanged - Listener - semaphore2 released - " + semaphore.availablePermits());
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                //Log.d(TAG, "onAccuracyChanged - Listener");
            }
        };
    }

    private boolean specificCases(){
        int type = sensor.getType();
        if(type == Sensor.TYPE_PROXIMITY || type == Sensor.TYPE_STEP_COUNTER){
            // special case where it wont update the same time and need to change the size to 1
            return true;
        }
        return false;
    }

    public abstract void initialValues(int size);

    public abstract void initialValues(int timesToTakeMesures, int valuesNumber);

    public abstract void setValues(SensorEvent event, int index);

    @Override
    public Map<String, Double> getData() {
        final Map<String, Double>[] map = new Map[]{new Hashtable<>(4 /*Parameters*/ * 3 /*Axis*/)};
        //Log.d(TAG, "getData - Entered getData()");
        Thread t = new Thread(() -> {
            map[0] = threadRun();
        });
        t.setDaemon(true);
        t.start();
        //Log.d(TAG, "getData - started t thread");
        try {
            t.join();
            //Log.d(TAG, "getData - After t.join()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.d(TAG, "getData - returned DATA");
        return map[0];
    }

    private Map<String,Double> threadRun(){
        Thread tt = new Thread(() -> sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL));
        tt.setDaemon(true);
        tt.start();
        try {
            tt.join();
            //Log.d(TAG, "threadRun - inside t - after tt.join()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, Double> map = new Hashtable<>(4 /*Parameters*/ * 3 /*Axis*/);

        //Log.d(TAG, "threadRun - inside t -  registered listener");

        try {
            //Log.d(TAG, "threadRun - inside t -  before acquiring semaphore2 - " + semaphore.availablePermits());
            semaphore2.acquire();
            //Log.d(TAG, "threadRun - inside t -  Acquired semaphore2 - " + semaphore.availablePermits());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        map.putAll(getStatisticsOfData());

        semaphore.release();
        //Log.d(TAG, "threadRun - inside t - released semaphore - " + semaphore.availablePermits());
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

    protected double getDeviation(@NonNull float[] data){
        double sum = 0;
        double mean = getMean(data);
        for(int i = 0; i < data.length; i ++){
            sum += Math.pow(data[i] - mean, 2);
        }
        return Math.sqrt(sum / data.length);
    }

    protected double getRange(@NonNull float[] data){
        return getMax(data) - getMin(data);
    }

    protected double getCorrelationBetweenTwoAxis(@NonNull float[] data1, @NonNull float[] data2){
        double data1_mean = getMean(data1);
        double data2_mean = getMean(data2);
        double mone = 0, mechane1 = 0, mechane2 = 0;
        for(int i = 0; i < data1.length; i ++){
            mone += (data1[i] - data1_mean) * (data2[i] - data2_mean);
            mechane1 += Math.pow(data1[i] - data1_mean, 2);
            mechane2 += Math.pow(data2[i] - data2_mean, 2);
        }
        return mone / Math.sqrt(mechane1 * mechane2);
    }

    protected  Map<String, Double> getCorrelations(float[][] data){
        Map<String, Double> map =  new Hashtable<>(15);
        for(int i = 0; i < data.length - 1; i ++){
            for(int j = i + 1; j <data.length; j ++){
                map.put("Correlation_" + i + "_" + j, getCorrelationBetweenTwoAxis(data[i], data[j]));
            }
        }


        return map;
    }


    protected double getMaximumRange(){
        return sensor.getMaximumRange();
    }

    protected double getPower(){
        return sensor.getPower();
    }

    @Override
    public String getName(){
        return sensorName;
    }

}
