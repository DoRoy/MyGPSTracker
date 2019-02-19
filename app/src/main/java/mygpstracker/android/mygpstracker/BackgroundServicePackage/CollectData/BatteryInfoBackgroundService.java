package mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.BackgroundServicePackage.ABackgroundService;
import mygpstracker.android.mygpstracker.Battery.BatteryInfoWrapper;
import mygpstracker.android.mygpstracker.Battery.MyBatteryInfo;

public class BatteryInfoBackgroundService extends ABackgroundService {

    private MyBatteryInfo myBatteryInfo;

    @Override
    public void onCreate() {

        myBatteryInfo = new MyBatteryInfo(this);
        setPeriod(getDoubleInMinutes(5));
        super.onCreate();
    }

    @Override
    protected Class<? extends ABackgroundService> getClassChild() {
        return this.getClass();
    }

    @Override
    protected List<TimerTask> getTimerTask() {
        ArrayList<TimerTask> list = new ArrayList<>();
        list.add(new TimerTask() {
            @Override
            public void run() {
                Map<String,String> data = myBatteryInfo.getData();
                BatteryInfoWrapper batteryInfoWrapper = new BatteryInfoWrapper(data);
                sqliteHelper.createBatteryInfo(batteryInfoWrapper);
            }
        });
        return list;
    }


}
