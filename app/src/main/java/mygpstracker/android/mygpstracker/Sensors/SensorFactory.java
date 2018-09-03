package mygpstracker.android.mygpstracker.Sensors;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by doroy on 02-Sep-18.
 */

public class SensorFactory {

    private final static String TAG = "SensorFactory";

    private final static int axisType = 1;
    private final static int pressureType = 2;
    private final static int lightType = 3;
    private final static int temperatureType = 4;
    private final static int percentType = 5;
    private final static int magneticType = 6;

    public static List<ISensor> getAllAvailableSensors(SensorManager sensorManager){
        List<Sensor> sensorsList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<ISensor> mySensorsList = new LinkedList<>();
        for (Sensor sensor: sensorsList) {
            Log.d(TAG, sensor.getName());
            switch(getSensorsType(sensor.getType())){
                case axisType:
                    //mySensorsList.add(new Sensor3Axis(sensor.getName(), sensor.getType()));
                    mySensorsList.add(new Sensor3Axis(sensor));
                    break;
                case pressureType:
                    mySensorsList.add(new SensorTemprature(sensor));
                    break;
                case lightType:
                    mySensorsList.add(new SensorTemprature(sensor));
                    break;
                case temperatureType:
                    mySensorsList.add(new SensorTemprature(sensor));
                    break;
                case percentType:
                    mySensorsList.add(new SensorTemprature(sensor));
                    break;
                case magneticType:

                    break;

                default:
                    break;

            }

        }
        return mySensorsList;
    }

    private static int getSensorsType(int type) {

        if(type == Sensor.TYPE_ACCELEROMETER || type == Sensor.TYPE_GRAVITY ||
                type == Sensor.TYPE_GYROSCOPE || type == Sensor.TYPE_LINEAR_ACCELERATION ||
                type == Sensor.TYPE_ROTATION_VECTOR || type == Sensor.TYPE_MAGNETIC_FIELD ||
                type == Sensor.TYPE_ORIENTATION || type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            return axisType;
        }
        else if (type == Sensor.TYPE_PRESSURE || type == Sensor.TYPE_PROXIMITY){
            return pressureType;
        }
        else if(type == Sensor.TYPE_AMBIENT_TEMPERATURE || type == Sensor.TYPE_TEMPERATURE){
            return temperatureType;
        }
        else if (type == Sensor.TYPE_LIGHT){
            return lightType;
        }
        else if (type == Sensor.TYPE_RELATIVE_HUMIDITY){
            return percentType;
        }
        else if(type == Sensor.TYPE_MAGNETIC_FIELD || type == Sensor.TYPE_GYROSCOPE_UNCALIBRATED || type == Sensor.TYPE_ACCELEROMETER_UNCALIBRATED){
            return magneticType;
        }
        return 0;

    }
}
