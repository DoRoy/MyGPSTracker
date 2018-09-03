package mygpstracker.android.mygpstracker.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by doroy on 03-Sep-18.
 */


public class SensorTemprature extends ASensorMeasures {

    private float[] values; // value[0]

    public SensorTemprature(Sensor sensor) {
        super(sensor);
        TAG = "SensorTemprature";
    }

    @Override
    public void initialValues(int size) {
        values = new float[size];
    }

    @Override
    public void setValues(SensorEvent event, int index) {
        values[index] = event.values[0];
    }

    @Override
    protected Map<String, Double> getStatisticsOfData() {
        Map<String, Double> map =  new Hashtable<>(4);
        map.put(sensorName + MAX_VALUE, getMax(values));
        map.put(sensorName + MIN_VALUE, getMin(values));
        map.put(sensorName + MEAN_VALUE, getMean(values));
        map.put(sensorName + MEDIAN_VALUE, getMedian(values));
        return map;
    }
}
