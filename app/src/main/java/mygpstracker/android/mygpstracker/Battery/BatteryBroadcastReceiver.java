package mygpstracker.android.mygpstracker.Battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    /**
     * This broadcast with the BatteryInfoBackgroundService suppose to make it collect information
     * while the app is killed.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(BatteryBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");

        /*The next line is the line that will make it work when the app is killed*/
//        context.startService(new Intent(context, BatteryInfoBackgroundService.class));;
    }
}
