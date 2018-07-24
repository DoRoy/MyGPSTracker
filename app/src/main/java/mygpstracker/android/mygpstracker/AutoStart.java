package mygpstracker.android.mygpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by doroy on 23-Jul-18.
 * Responsible to start the service as the phone turns on.
 */

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.getInstance().write("AutoStart\n");
        context.startService(new Intent(context,BackgroundService.class));
    }


}
