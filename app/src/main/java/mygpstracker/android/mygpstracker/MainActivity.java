package mygpstracker.android.mygpstracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public TextView textView_mainActivity;
    public Button refresh_btn;
    public Button currentLocation_btn;
    private File file;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the buttons
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
        currentLocation_btn = (Button) findViewById(R.id.currentLocation_btn);
        textView_mainActivity = (TextView) findViewById(R.id.textView_mainActivity);

        initializeListeners();


        SamplePolicy samplePolicy = new SamplePolicy(6,0.25);
        BackgroundService.samplePolicy = samplePolicy;
        BackgroundService.gpsLocation = new GPSLocation(this); // initialize the strategy we want to take locations with
        file = getApplicationContext().getFileStreamPath("myLogFileText"); // get the file from the apps cache
        Log.getInstance().setFile(file); // set file as log file

        // check permissions and ask for them if there isn't permissions
        boolean fineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (fineLocation && coarseLocation) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }

        // start the background service
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(),BackgroundService.class));
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,10000);
/*        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (int)(10000),
                PendingIntent.getService(this, 0, new Intent(this, BackgroundService.class), 0)
        );*/
    }

    // the reset button, resets the content of the location log
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.reset){

            try {
                OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file));
                fileWriter.write("");
                fileWriter.close();
                refresh();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return true;
    }

    // initialize the reset button
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * Gets the current location and display it on the app main view
     */
    private void getCurrentLocation() {
        GPSLocation gpsLocation = new GPSLocation(this);
        Location location = gpsLocation.getLastKnownLocation();
        textView_mainActivity.setText("Current Location:\n" +
                                       "\t\tLATITUDE = " + location.getLatitude() +
                                        "\n\t\tLONGITUDE = " + location.getLongitude());
    }

    /**
     * Refreshes the main view of the app with the content of the location log
     */
    public void refresh(){
        Thread t = new Thread(()->{
            String content = Log.getInstance().read();
            this.runOnUiThread(()->textView_mainActivity.setText(content));
        });
        t.start();
    }

    /**
     * Initialize the button listeners
     */
    private void initializeListeners(){
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
    }
}
