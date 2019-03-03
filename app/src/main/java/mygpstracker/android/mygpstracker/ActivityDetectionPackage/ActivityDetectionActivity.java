package mygpstracker.android.mygpstracker.ActivityDetectionPackage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;

import mygpstracker.android.mygpstracker.R;


public class ActivityDetectionActivity extends Activity {
    private String TAG = ActivityDetectionActivity.class.getSimpleName();
    BroadcastReceiver broadcastReceiver;

    private TextView txtActivity, txtConfidence;
    private ImageView imgActivity;
    private Button btnStartTracking, btnStopTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activitiy_detection);

        initialWidgets();

        // create a broadcast receiver to handle an activity detection intent.
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BroadCastReceiver","onReceive");
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };
        // start tracking the movement.
        startTracking();
    }

    /**
     * Initial the widgets of the activity.
     */
    private void initialWidgets(){
        txtActivity = findViewById(R.id.txt_activity);
        txtConfidence = findViewById(R.id.txt_confidence);
        imgActivity = findViewById(R.id.img_activity);
        btnStartTracking = findViewById(R.id.btn_start_tracking);
        btnStopTracking = findViewById(R.id.btn_stop_tracking);

        btnStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
            }
        });

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });
    }

    /**
     * Handles with recognizing an activity.
     * @param type  - an int representing the type of activity that was recognized.
     * @param confidence    - an int representing the confidence of the activity.
     */
    private void handleUserActivity(int type, int confidence) {
        Log.d(TAG,"handleUserActivity");
        String label = getString(R.string.activity_unknown);
        int icon = R.drawable.ic_still;

        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                label = getString(R.string.activity_in_vehicle);
                icon = R.drawable.ic_driving;
                break;
            case DetectedActivity.ON_BICYCLE:
                label = getString(R.string.activity_on_bicycle);
                icon = R.drawable.ic_on_bicycle;
                break;
            case DetectedActivity.ON_FOOT:
                label = getString(R.string.activity_on_foot);
                icon = R.drawable.ic_walking;
                break;
            case DetectedActivity.RUNNING:
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                break;
            case DetectedActivity.STILL:
                label = getString(R.string.activity_still);
                icon = R.drawable.ic_still;
                break;
            case DetectedActivity.TILTING:
                label = getString(R.string.activity_tilting);
                icon = R.drawable.ic_tilting;
                break;
            case DetectedActivity.WALKING:
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;
                break;
            case DetectedActivity.UNKNOWN:
                label = getString(R.string.activity_unknown);
                icon = R.drawable.ic_unknown;
                break;
        }

//        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

        if (confidence > Constants.CONFIDENCE) {
            Log.d(TAG, "Set New Activity: " + label + ", Confidence: " + confidence);
            txtActivity.setText(label);
            txtConfidence.setText("Confidence: " + confidence);
            imgActivity.setImageResource(icon);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
        // register the broadcast receiver to receive activity detection
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        Log.d(TAG,"onPause");
        super.onPause();
        // removing this will keep it listening when the activity is closed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void startTracking() {
        Log.d(TAG,"startTracking");
        // start the service
        Intent intent1 = new Intent(ActivityDetectionActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void stopTracking() {
        Log.d(TAG,"stopTracking");
        Intent intent = new Intent(ActivityDetectionActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        Intent intent = new Intent(ActivityDetectionActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }
}
