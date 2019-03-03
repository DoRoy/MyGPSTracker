package mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.BackgroundServicePackage.ABackgroundService;
import mygpstracker.android.mygpstracker.Battery.BatteryInfoWrapper;
import mygpstracker.android.mygpstracker.DB.CallsInfoWrapper;
import mygpstracker.android.mygpstracker.DB.SqliteHelper;
import mygpstracker.android.mygpstracker.Sensors.CallLogInformation;

public class CallLogBackgroundService extends ABackgroundService {

    private CallLogInformation callLogInformation;

    @Override
    public void onCreate() {
        callLogInformation = new CallLogInformation(getApplicationContext());
        setPeriod(getDoubleInHours(1.0));
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
                String timeStamp = sqliteHelper.getLastTimeStamp(SqliteHelper.TABLE_CALLS);
                int[] data = callLogInformation.getDurationsOnly(timeStamp);
                CallsInfoWrapper callsInfoWrapper = null;
                try {
                    callsInfoWrapper = new CallsInfoWrapper(data);
                    sqliteHelper.createCallsRecord(callsInfoWrapper);
                } catch (Exception e) {
                    //e.printStackTrace();
                }

            }
        });
        return list;
    }
}
