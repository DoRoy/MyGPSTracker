package mygpstracker.android.mygpstracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public TextView textView_mainActivity;
    public Button refresh_btn;
    public Button currentLocation_btn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
        currentLocation_btn = (Button) findViewById(R.id.currentLocation_btn);
        textView_mainActivity = (TextView) findViewById(R.id.textView_mainActivity);

        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        currentLocation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });


        BackgroundService.timesToTakeLocation = 6;
        BackgroundService.intervalsInMinutes = 0.5;
        BackgroundService.gpsLocation = new GPSLocation(this);
        File file = getApplicationContext().getFileStreamPath("myLogFileText");
        Log.getInstance().setFile(file);

        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(10000),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );
        //startService(new Intent(this, BackgroundService.class));
    }

    private void getCurrentLocation() {
        GPSLocation gpsLocation = new GPSLocation(this);
        Location location = gpsLocation.getLastKnownLocation();
        textView_mainActivity.setText("Location:\n" +
                                       "LAT = " + location.getLatitude() +
                                        "\nLONG = " + location.getLongitude());

    }

    public void refresh(){
        String content = Log.getInstance().read();
        textView_mainActivity.setText(content);
    }
}
