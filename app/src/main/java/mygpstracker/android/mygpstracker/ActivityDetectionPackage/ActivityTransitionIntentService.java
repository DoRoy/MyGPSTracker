package mygpstracker.android.mygpstracker.ActivityDetectionPackage;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import mygpstracker.android.mygpstracker.DB.SqliteHelper;


/**
 * This IntentService will get updates from the ActivityDetectionClient in the 'BackgroundActivityTransitionService' class
 * The updates are saved with the SqliteHelper
 */
public class ActivityTransitionIntentService extends IntentService {

    protected static final String TAG = ActivityTransitionIntentService.class.getSimpleName();
    SqliteHelper sqliteHelper;

    public ActivityTransitionIntentService() {
        super(TAG);
        sqliteHelper = new SqliteHelper(getApplicationContext());
    }

    @Override
    protected void onHandleIntent( Intent intent) {
        Log.d(TAG,"onHandleIntent");
        // check if the result exist
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            if (result == null) return;
            for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                int type = event.getActivityType();
                String typeLabel = getActivityLabel(type);
                // save the transition
                sqliteHelper.createActivityRecord(typeLabel);
//                broadcastActivity(event);
            }
        }
    }

    /**
     * Convert the type int to a String to understand what is the activity
     * @param type  - The type received from the ActivityTransitionEvent
     * @return  - A String representation of the type or the int as String if the type isn't familiar
     */
    private String getActivityLabel(int type){
        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                return "In Vehicle";
            case DetectedActivity.ON_BICYCLE:
                return  "On Bicycle";
            case DetectedActivity.ON_FOOT:
                return "On Foot";
            case DetectedActivity.RUNNING:
                return "Running";
            case DetectedActivity.STILL:
                return "Still";
            case DetectedActivity.TILTING:
                return "Tilting";
            case DetectedActivity.WALKING:
                return "Walking";
        }
        return "" + type;
    }

//TODO - remove this, its here only for debug to see it in the activity
    private void broadcastActivity(ActivityTransitionEvent activity) {
        Log.d(TAG,"broadcastActivity");
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getActivityType());
        intent.putExtra("transitionType", activity.getTransitionType());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
