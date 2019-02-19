package mygpstracker.android.mygpstracker.Sensors;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by doroy on 02-Sep-18.
 */

public class SensorFactory {

    private final static String TAG = "SensorFactory";


    private final static int ONE_VALUES = 1;
    private final static int THREE_VALUES = 3;
    private final static int SIX_VALUES = 6;

    private static SensorManager sensorManager;

    public static void setSensorManager(SensorManager newSensorManager){
        sensorManager = newSensorManager;
    }

    public static List<ISensor> getAllAvailableSensors(){
        List<Sensor> sensorsList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<ISensor> mySensorsList = new LinkedList<>();
        for (Sensor sensor: sensorsList) {
            if(sensor.getReportingMode() == Sensor.REPORTING_MODE_CONTINUOUS) {
                mySensorsList.add(new SensorContinuous(sensor));
            }

        }
        return mySensorsList;
    }


    @SuppressLint("NewApi")
    public static SparseArray<String> getAllAvailableSensorsNameAndID(){
        List<Sensor> sensorsList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        SparseArray<String> sparseArray = new SparseArray<>(sensorsList.size());
        for (Sensor sensor: sensorsList) {
            if(sensor.getReportingMode() == Sensor.REPORTING_MODE_CONTINUOUS) {
                sparseArray.append(sensor.getId(),sensor.getName());
            }
        }
        return sparseArray;
    }

    private static int getSensorsType(int type) {

        if(type == Sensor.TYPE_ACCELEROMETER || type == Sensor.TYPE_GRAVITY ||
                type == Sensor.TYPE_GYROSCOPE || type == Sensor.TYPE_LINEAR_ACCELERATION ||
                type == Sensor.TYPE_MAGNETIC_FIELD ||
                type == Sensor.TYPE_ORIENTATION || type == Sensor.TYPE_ORIENTATION ){
            return THREE_VALUES;
        }
        else if (type == Sensor.TYPE_PRESSURE || type == Sensor.TYPE_PROXIMITY ||
                type == Sensor.TYPE_LIGHT || type == Sensor.TYPE_AMBIENT_TEMPERATURE ||
                type == Sensor.TYPE_TEMPERATURE || type == Sensor.TYPE_RELATIVE_HUMIDITY ||
                type == Sensor.TYPE_STEP_COUNTER ){
            return ONE_VALUES;
        }
        else if(type == Sensor.TYPE_GYROSCOPE_UNCALIBRATED || type == Sensor.TYPE_ACCELEROMETER_UNCALIBRATED ||
                type == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED){
            return SIX_VALUES;
        }

        return 0;
    }
}


/* TODO - try to create subclasses of sensors for: Continues, On-Change, One-Shot, Special.
* */