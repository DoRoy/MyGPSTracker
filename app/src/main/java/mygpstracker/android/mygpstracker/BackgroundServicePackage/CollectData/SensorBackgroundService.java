package mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData;

import android.util.ArrayMap;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.BackgroundServicePackage.ABackgroundService;
import mygpstracker.android.mygpstracker.Sensors.ISensor;
import mygpstracker.android.mygpstracker.Sensors.SensorFactory;

public class SensorBackgroundService extends ABackgroundService {

    List<ISensor> sensorsList;

    @Override
    public void onCreate() {
        setPeriod(getDoubleInMinutes(5));
        super.onCreate();
    }


    @Override
    protected Class<? extends ABackgroundService> getClassChild() {
        return this.getClass();
    }

    @Override
    protected List<TimerTask> getTimerTask() {
        ArrayList<TimerTask> timerTaskList = new ArrayList<>();
        sensorsList = SensorFactory.getAllAvailableSensors();
        for (ISensor sensor: sensorsList) {
            final ISensor sensor1 = sensor;
            timerTaskList.add(new TimerTask() {
                @Override
                public void run() {
                    try {
                        Map<String, Double> map = sensor1.getData();
                        StringBuilder record = new StringBuilder();
                        for (Map.Entry<String, Double> pair : map.entrySet()) {
                            //System.out.println(pair.getKey() + ": " + pair.getValue());
                            record.append(pair.getKey()).append(": ").append(pair.getValue()).append("|");
                        }
                        sqliteHelper.createSensorRecord(sensor1.getName(), record.substring(0, record.length() - 1));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        return timerTaskList;


    }
}
