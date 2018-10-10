package mygpstracker.android.mygpstracker.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by doroy on 08-Oct-18.
 */

public class SensorContinuous extends ASensorMeasures  {

    private float[][] value;


    public SensorContinuous(Sensor sensor){
        super(sensor);
        TAG = "SensorContinuous";
    }

    @Override
    public void initialValues(int size) {

    }

    @Override
    public void initialValues(int timesToTakeMessures, int valuesNumber) {
        value = new float[valuesNumber][timesToTakeMessures];
    }


    @Override
    public void setValues(SensorEvent event, int index) {
        int limit = event.values.length;
        for(int i = 0; i < limit; i++){
            value[i][index] = event.values[i];
        }
    }

    @Override
    protected Map<String, Double> getStatisticsOfData(){
        Map<String, Double> map =  new Hashtable<>(15);
        map.putAll(getDataByAxis());
        if(value.length > 1 ){
            map.putAll(getCorrelations(value));
        }
        return map;
    }

    private Map<String, Double> getDataByAxis(){
        float[] values = new float[value.length];
        Map<String, Double> map = new Hashtable<>(5);
        for(int i = 0; i < value.length; i ++) {
            values = value[i];
            map.put(sensorName + MAX_VALUE + "_[" + i + "]", getMax(values));
            map.put(sensorName + MIN_VALUE + "_[" + i + "]", getMin(values));
            map.put(sensorName + MEAN_VALUE + "_[" + i + "]", getMean(values));
            map.put(sensorName + MEDIAN_VALUE + "_[" + i + "]", getMedian(values));
            map.put(sensorName + DEVIATION_VALUE + "_[" + i + "]", getDeviation(values));
            map.put(sensorName + POWER_VALUE , getPower());
            map.put(sensorName + MAX_RANGE_VALUE, getMaximumRange());
            map.put(sensorName + "range", getRange(values));
        }
        return map;
    }


}
