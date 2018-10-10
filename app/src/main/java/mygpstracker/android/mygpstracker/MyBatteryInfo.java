package mygpstracker.android.mygpstracker;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by doroy on 09-Oct-18.
 */

public class MyBatteryInfo {

    private Context context;
    private IntentFilter iFilter;
    private Intent batteryStatus;
    private BatteryManager mBatteryManager;


    public MyBatteryInfo(Context context) {
        this.context = context;
        iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);

        updateStatus();
    }

    private void updateStatus(){
        batteryStatus = context.registerReceiver(null, iFilter);
    }

    private boolean isCharging(){
        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }


    /**
     * BATTERY_PLUGGED_AC = 1
     * BATTERY_PLUGGED_USB = 2
     * BATTERY_PLUGGED_WIRELESS = 4
     * Non of the above
     * @return
     */
    private int chargeType(){
        // How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if(chargePlug > 0)
            return chargePlug;
        return 0;
    }

    private int getBatteryLevel(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
    }

    private int getBatteryScale(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
    }

    private int getPercentage(){
        int level = getBatteryLevel() * 100;
        int scale = getBatteryScale();
        if(level == -1 || scale == -1)
            return -1;
        return level / scale ;
    }

    private int getTemprature(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,-1);
    }

    private int getVoltage(){
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
    }

    private String getTechnology(){
        return batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
    }

    private String getHealth(){
        boolean present = batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
        String healthLbl = "Unknown";
        if (present) {
            int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    healthLbl = "Cold";
                    break;

                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthLbl = "Dead";
                    break;

                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthLbl = "Good";
                    break;

                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthLbl = "Over Voltage";
                    break;

                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthLbl = "Overheat";
                    break;

                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthLbl = "Unspecified Failure";
                    break;
            }
        }
        return healthLbl;
    }

    private long getCapacity(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
            Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

            if (chargeCounter != null && capacity != null) {
                long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                return value;
            }
        }

        return 0;
    }


    public Map<String,String> getData(){
        Map<String, String> map = new Hashtable<>();
        updateStatus();
        map.put("isCharging",  "" + isCharging() );
        int type = chargeType();
        String typeString = "Not Charging";
        switch (type){
            case 1:
                typeString = "AC";
                break;
            case 2:
                typeString = "USB";
                break;
            case 4:
                typeString = "WIRELESS";
                break;
        }
        map.put("Charging Type", typeString);
        map.put("Level", "" + getBatteryLevel());
        map.put("Scale", "" + getBatteryScale());
        map.put("Percent", "" + getPercentage() + "%");
        map.put("Temprature", "" + ((float) getTemprature()) / 10f + " °C");
        map.put("Voltage", "" + getVoltage() + " Mv");
        map.put("Health", getHealth());
        map.put("Capacity", "" + getCapacity() + " mAh");
        map.put("Technology", getTechnology());

        return map;
    }

    public String getDataAsString(){
        String data = "Battery data:\n";
        Map<String, String> map = getData();
        for(Map.Entry<String, String> entry: map.entrySet()){
            data += "\t\t" + entry.getKey() + ": " + entry.getValue() + "\n";
        }
        return data;
    }


}
