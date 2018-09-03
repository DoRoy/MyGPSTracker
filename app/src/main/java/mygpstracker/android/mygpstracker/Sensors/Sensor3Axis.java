package mygpstracker.android.mygpstracker.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


import java.util.Date;
import java.util.Hashtable;


import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


import mygpstracker.android.mygpstracker.AppExecutors;
import mygpstracker.android.mygpstracker.SamplePolicy;

/**
 * Created by doroy on 29-Aug-18.
 */

public class Sensor3Axis extends ASensorMeasures {

    private float[] valueX; // value[0]
    private float[] valueY; // value[1]
    private float[] valueZ; // value[2]

    private final String x = "_x";
    private final String y = "_y";
    private final String z = "_z";

    public Sensor3Axis(Sensor sensor){
        super(sensor);
        TAG = "Sensor3Axis";;
    }

    @Override
    public void initialValues(int size) {
        valueX = new float[size];
        valueY = new float[size];
        valueZ = new float[size];
    }

    @Override
    public void setValues(SensorEvent event, int index) {
        valueX[index] = event.values[0];
        valueY[index] = event.values[1];
        valueZ[index] = event.values[2];
    }

    @Override
    protected Map<String, Double> getStatisticsOfData(){
        Map<String, Double> map =  new Hashtable<>(12);
        map.putAll(getDataByAxis(x));
        map.putAll(getDataByAxis(y));
        map.putAll(getDataByAxis(z));
        return map;
    }

    private Map<String, Double> getDataByAxis(String axis){
        float[] values = new float[valueX.length];
        switch (axis){
            case x:
                values = valueX;
                break;
            case y:
                values = valueY;
                break;
            case z:
                values = valueZ;
                break;
        }
        Map<String, Double> map =  new Hashtable<>(4);
        map.put(sensorName + MAX_VALUE + axis, getMax(values));
        map.put(sensorName + MIN_VALUE + axis, getMin(values));
        map.put(sensorName + MEAN_VALUE + axis, getMean(values));
        map.put(sensorName + MEDIAN_VALUE + axis, getMedian(values));
        return map;

    }



}
