package mygpstracker.android.mygpstracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.Sensors.ActivityTest;

public class MainActivity extends AppCompatActivity {

    public TextView textView_mainActivity;
    public Button refresh_btn;
    public Button currentLocation_btn;

    private final String MY_PREFS_NAME = "MyPrefs";
    private final String INTERVALS = "INTERVALS";
    private final String TIMESTOTAKELOCATION = "TIMESTOTAKELOCATION";

    private File file;
    public static int timesToTakeLocation = 6;
    public static double intervals = 0.25;
    private SamplePolicy samplePolicy;
    public static boolean didChanged = false;

    private ContentResolver myContentProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //remove this
        myContentProvider = this.getContentResolver();
 /*       myContentProvider.delete(MyContentProvider.CONTENT_URI,null,null);
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.COLUMN_DATE, "today2");
        values.put(MyContentProvider.COLUMN_LONGITUDE, "3454");
        values.put(MyContentProvider.COLUMN_LATITUDE, "5674");
        ContentValues values2 = new ContentValues();
        values2.put(MyContentProvider.COLUMN_DATE, "tommarrow");
        values2.put(MyContentProvider.COLUMN_LONGITUDE, "345");
        values2.put(MyContentProvider.COLUMN_LATITUDE, "567");
        myContentProvider.insert(MyContentProvider.CONTENT_URI, values);
        myContentProvider.insert(MyContentProvider.CONTENT_URI,values2);
        Cursor cursor = myContentProvider.query(MyContentProvider.CONTENT_URI,null,null,null,MyContentProvider.COLUMN_DATE);
        while(cursor.moveToNext()) {
            String s1 = cursor.getString(MyContentProvider.LOCATIONS_ID);
            String s2 = cursor.getString(MyContentProvider.LOCATIONS_DATE);
            String s3 = cursor.getString(MyContentProvider.LOCATIONS_LONGITUDE);
            String s4 = cursor.getString(MyContentProvider.LOCATIONS_LATITUDE);
            System.out.println(s1+ ". " + s2 + ": " + s3 + ", " + s4 +"\n");
        }*/



        // initialize the buttons
        refresh_btn = (Button) findViewById(R.id.refresh_btn);
        currentLocation_btn = (Button) findViewById(R.id.currentLocation_btn);
        textView_mainActivity = (TextView) findViewById(R.id.textView_mainActivity);

        initializeListeners();
        initializeSharedPreferences();

        samplePolicy = new SamplePolicy(timesToTakeLocation, intervals);
        BackgroundService.samplePolicy = samplePolicy;
        BackgroundService.gpsLocation = new GPSLocation(this); // initialize the strategy we want to take locations with
        file = getApplicationContext().getFileStreamPath("myLogFileText"); // get the file from the apps cache
        Log.getInstance().setFile(file); // set file as log file
        Log.getInstance().setResolver(myContentProvider);

        // check permissions and ask for them if there isn't permissions
        boolean fineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean callLog = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED;
        if (fineLocation && coarseLocation && callLog) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALL_LOG}, 2);
        }


        // start the background service
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(),BackgroundService.class));
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,3000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("OnStart");
        samplePolicy.setTimesToTakeLocation(timesToTakeLocation);
        samplePolicy.setIntervalsInMinutes(intervals);
    }



    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop");
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putFloat(INTERVALS, (float)intervals);
        editor.putInt(TIMESTOTAKELOCATION, timesToTakeLocation);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(didChanged){
            Date currentTime = Calendar.getInstance().getTime();
            String[] dateArr = currentTime.toString().split(" ");
            String timeAndDate = dateArr[1] + " " + dateArr[2] + " " + dateArr[5] + " " + dateArr[3];
            //Log.getInstance().write("\t** " + timeAndDate + ": Interval = " + intervals + ", Times = " + timesToTakeLocation + " **\n");
            samplePolicy.setIntervalsInMinutes(intervals);
            samplePolicy.setTimesToTakeLocation(timesToTakeLocation);

            stopService(new Intent(getApplicationContext(),BackgroundService.class));
            startService(new Intent(getApplicationContext(),BackgroundService.class));

            didChanged = false;
        }
        refresh();

    }



    // the reset button, resets the content of the location log
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case R.id.reset:
/*                try {
                    OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file));
                    fileWriter.write("");
                    fileWriter.close();
                    refresh();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                Log.getInstance().resetDataBase();
                refresh();
                break;
            case R.id.settings:
                Intent settingsIntent = new Intent(this, Settings.class);
                startActivity(settingsIntent);
                break;
            case R.id.secondScreen:
                Intent secondScreenIntent = new Intent(this, ActivityTest.class);
                startActivity(secondScreenIntent);
                break;
        }

        return true;
    }

    private void initializeSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Map<String, ?> map = sharedPreferences.getAll();
        if(map != null && map.size() > 0) {
            intervals = (Float) map.get(INTERVALS);
            timesToTakeLocation = (Integer) map.get(TIMESTOTAKELOCATION);
        }
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
            //String content = Log.getInstance().read();
            String content = Log.getInstance().readWithResolver();
            runOnUiThread(()->textView_mainActivity.setText(content));
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

    private void checkPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                checkPermissions();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALL_LOG},
                        200);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            //permission granted. do your stuff
        }
    }


    //TODO Fix permissions and making sure location is on, try to set it to stop when off and to start when it changes to on.

}
