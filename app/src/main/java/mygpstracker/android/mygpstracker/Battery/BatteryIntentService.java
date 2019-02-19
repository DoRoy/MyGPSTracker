package mygpstracker.android.mygpstracker.Battery;

import android.app.IntentService;
import android.content.Intent;
import android.os.BatteryManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Set;

public class BatteryIntentService extends IntentService {

    protected static final String TAG = BatteryIntentService.class.getSimpleName();


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BatteryIntentService(String name) {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
    }
}
