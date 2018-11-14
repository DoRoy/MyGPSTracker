package mygpstracker.android.mygpstracker;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import mygpstracker.android.mygpstracker.GPSTesting.GPSTest;
import mygpstracker.android.mygpstracker.Places.ActivityPlaces;
import mygpstracker.android.mygpstracker.Places.MyPlaces;
import mygpstracker.android.mygpstracker.Sensors.ActivityTest;
import mygpstracker.android.mygpstracker.Sensors.MyNetworkInfo;

public class MainActivity extends AppCompatActivity {

    public TextView textView_mainActivity;
    public Button refresh_btn;
    public Button currentLocation_btn;

    private final String MY_PREFS_NAME = "MyPrefs";
    private final String INTERVALS = "INTERVALS";
    private final String TIMESTOTAKELOCATION = "TIMESTOTAKELOCATION";
    private final String TAG = "MainActivity";

    private File file;
    public static int timesToTakeLocation = 6;
    public static double intervals = 0.25;
    private SamplePolicy samplePolicy;
    public static boolean didChanged = false;

    GPSTest gpsTest;

    MyPlaces myPlace;

    private ContentResolver myContentProvider;

    private int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //remove this
        myContentProvider = this.getContentResolver();

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
        MyLog.getInstance().setFile(file); // set file as log file
        MyLog.getInstance().setResolver(myContentProvider);

        // check permissions and ask for them if there isn't permissions
        boolean fineLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean callLog = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED;
        if (!fineLocation || !coarseLocation || !callLog) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALL_LOG}, 2);
        }

        if(gpsTest == null)
            gpsTest = new GPSTest(this);
        if(myPlace == null)
            myPlace = new MyPlaces(this);




        // start the background service
        setLocationSettingsListener();

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
        Log.d(TAG, "onStop");
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
            //MyLog.getInstance().write("\t** " + timeAndDate + ": Interval = " + intervals + ", Times = " + timesToTakeLocation + " **\n");
            samplePolicy.setIntervalsInMinutes(intervals);
            samplePolicy.setTimesToTakeLocation(timesToTakeLocation);

            stopService(new Intent(getApplicationContext(),BackgroundService.class));
            startService(new Intent(getApplicationContext(),BackgroundService.class));

            didChanged = false;
        }
        else if(flag == 0){
            flag = 1;
           registerReceiverGPS();
        }
        refresh();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (yourReceiver != null) {
            unregisterReceiver(yourReceiver);
            yourReceiver = null;
        }

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
                MyLog.getInstance().resetDataBase();
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
            case R.id.placesScreen:
                Intent placesScreenIntent = new Intent(this, ActivityPlaces.class);
                startActivity(placesScreenIntent);
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
        textView_mainActivity.setText("");
        //getLocationOldWay();

        getBatteryData();
        getNetWorkData();
        getLocationWithGPSTracker();
        AppExecutors.getInstance().networkIO().execute(()->getLocationWithPlaces());
    }

    private void getLocationWithPlaces() {
        Map<Place,Float> placesList =  myPlace.guessCurrentPlace();
        if(placesList != null){
            String content = "\nPlaces:\n";
            int i = 1;
            for(Map.Entry<Place,Float> entry : placesList.entrySet()){
                Place place = entry.getKey();
                content += "\t\t" + i + ". " + place.getName() + "\n";
                content += "\t\t\tLikelihood: "  + (int)(entry.getValue() * 100) + "%\n";
                content += "\t\t\tID: "  + place.getId() + "\n";
                content += "\t\t\tLatLon: "  + place.getLatLng().latitude + ", " + place.getLatLng().longitude + "\n";
                content += "\t\t\tAddress: " + place.getAddress() + "\n";
                content += "\t\t\tAttribution: " + place.getAttributions() + "\n";
                content += "\t\t\tRating: " + place.getRating() + "\n";
                content += "\t\t\tPhone: " + place.getPhoneNumber() + "\n";
                content += "\t\t\tPrice Level: " + place.getPriceLevel() + "\n";
                content += "\t\t\tWebsite Uri: " + place.getWebsiteUri() + "\n";
                List<Integer> typeList = place.getPlaceTypes();
                content += "\t\t\tTypes: ";
                for(Integer intType:typeList){
                    content += intType + " ";
                }
                content += "\n\n";

                i++;
            }

            String finalContent = content;
            runOnUiThread(()->textView_mainActivity.append(finalContent));
        }
    }


    private void getLocationWithGPSTracker() {

        Location location = gpsTest.getLastKnownLocation();
 //       gpsTest.getAddress();
        if(location != null) {
            runOnUiThread(() -> textView_mainActivity.append("\nLAN:" + location.getLatitude() + ", LON: " + location.getLongitude()));
            runOnUiThread(()->textView_mainActivity.append("\n" + gpsTest.getAddressString() + "\n"));
        }
        else
            runOnUiThread(()->textView_mainActivity.append("\nLocation came back null\n"));

        //gpsTest.getPeriodicLocation(10000,5000,LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getNetWorkData() {
        MyNetworkInfo networkInfo = new MyNetworkInfo(this);
        String networkInfoString = networkInfo.getNetworkDataString();

        runOnUiThread(()->textView_mainActivity.append(networkInfoString));
    }

    private void getBatteryData() {
        MyBatteryInfo batteryInfo = new MyBatteryInfo(this);
        String batteryDataString = batteryInfo.getDataAsString();

        runOnUiThread(()-> textView_mainActivity.append(batteryDataString));
    }


    /**
     * Refreshes the main view of the app with the content of the location log
     */
    public void refresh(){
        Thread t = new Thread(()->{
            //String content = MyLog.getInstance().read();
            String content = MyLog.getInstance().readWithResolver();
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


    private void setLocationSettingsListener(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                Log.d(TAG, "OnSuccess LocationSettingsResponse");
                if(locationSettingsResponse.getLocationSettingsStates().isLocationPresent()){
                    Log.d(TAG,"OnSuccess GPS is Usable");
                    checkGPS();
                }
                else{
                    Log.d(TAG,"OnSuccess GPS is not Usable");
                }

            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "OnFailure");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this,200);
                        Log.d(TAG, "After resolvble");
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.d(TAG, "Inside catch");
                        // Ignore the error.
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
        if(requestCode == 200 && resultCode == 0){
            //checkGPS();
        }
    }

    private boolean isGPSUsable(){
        Context context = this.getApplicationContext();
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean b = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locationManager = null;
            if (b) {
                Log.d(TAG, "isGPSUsable - GPS is on");

            } else {
                Log.d(TAG, "isGPSUsable - GPS is off");
                //stopService(new Intent(getApplicationContext(),BackgroundService.class));
            }
            return b;
        }
        return false;
    }



    private void startGPS() {
        if(isGPSAlreadyOn) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    startService(new Intent(getApplicationContext(), BackgroundService.class));
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 3000);
        }
    }


    private static final String ACTION_GPS = "android.location.PROVIDERS_CHANGED";
    private BroadcastReceiver yourReceiver;
    private static volatile boolean isGPSAlreadyOn = false;

    private void checkGPS() {
        Context context = this.getApplicationContext();
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean b = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locationManager = null;
            if (b) {
                if (!isGPSAlreadyOn) {
                    Log.d(TAG, "checkGPS - GPS is on");
                    isGPSAlreadyOn = true;
                    startGPS();
                }

            } else {
                Log.d(TAG, "checkGPS - GPS is off");
                isGPSAlreadyOn = false;
                stopService(new Intent(getApplicationContext(),BackgroundService.class));

            }
        }
    }

    private void registerReceiverGPS() {
        if (yourReceiver == null) {
            // INTENT FILTER FOR GPS MONITORING
            final IntentFilter theFilter = new IntentFilter();
            theFilter.addAction(ACTION_GPS);
            yourReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d(TAG, "registerReceiverGPS - onReceive");
                    if (intent != null) {
                        String s = intent.getAction();
                        if (s != null) {
                            if (s.equals(ACTION_GPS)) {
                                checkGPS();
                            }
                        }
                    }
                    //unregisterReceiver(this);
                }
            };
            registerReceiver(yourReceiver, theFilter);
        }
    }




}
