package mygpstracker.android.mygpstracker;

import android.Manifest;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



/**
 * Created by doroy on 18-Jul-18.
 * Implementing the strategy of getting a location using the GPS Sensor.
 */
public class GPSLocation implements ILocation {

    private AppCompatActivity application;
    private FusedLocationProviderClient mFusedLocationClient;


    public GPSLocation(AppCompatActivity application) {
        this.application = application;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
    }

    /**
     * Gets the Current Location
     * @return The Current Location
     */
    public Location getLastKnownLocation(){
        boolean fineLocation = ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (fineLocation && coarseLocation) {
            ActivityCompat.requestPermissions(application, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        Task<Location> task = mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
            }
        });

        while(!task.isComplete());
        Location location = task.getResult();
        return location;
    }



    @Override
    /**
     * @return: array format - {meanLatitude, meanLongitude}
     */
    public double[] getMeanLocation(Location[] locations) {
        double meanLon = 0, meanLan = 0;

/*      meanLan = meanLatitude(locations);
        meanLon = meanLongitude(locations);*/

        double[] ans = calculateMean(locations);
        return ans;
    }

/*    private double meanLongitude(Location[] locations) {
        double meanLon = 0, cosLon = 0, sinLon = 0;
        for(int i = 0; i < locations.length; i++){
            sinLon += Math.sin(locations[i].getLongitude());
            cosLon += Math.cos(locations[i].getLongitude());
        }
        sinLon = sinLon / locations.length;
        cosLon = cosLon / locations.length;
        meanLon = Math.atan2(sinLon,cosLon);
        return meanLon;
    }*/

    /**
     * Calculate a simple mean of the Longitude and Latitude.
     * @param locations - an array to locations
     * @return - an array of double of the form: {meanLat,meanLong}.
     */
    private double[] calculateMean(Location[] locations){
        double meanLong = 0;
        double meanLat = 0;
        int i = 0;
        for(i = 0; i < locations.length && locations[i] != null; i ++){
            meanLong += locations[i].getLongitude();
            meanLat += locations[i].getLatitude();
        }
        meanLong = meanLong / i;
        meanLat = meanLat / i;
        double[] ans = {meanLat,meanLong};
        return ans;
    }

    /**
     * Calculates the mean Longitude
     * @param locations - an array of locations
     * @return the mean of the longitude from the locations.
     */
    private double meanLongitude(Location[] locations){
        double meanLong = 0;
        for(int i = 0; i < locations.length; i ++){
            meanLong += locations[i].getLongitude();
        }
        meanLong = meanLong / locations.length;
        return meanLong;
    }

    /**
     * Calculates the mean Latitude
     * @param locations - an array of locations
     * @return the mean of the Latitude from the locations.
     */
    private double meanLatitude(Location[] locations){
        double meanLan = 0;
        for(int i = 0; i < locations.length; i ++){
            meanLan += locations[i].getLatitude();
        }
        meanLan = meanLan / locations.length;
        return meanLan;
    }

    //TODO - check if i can get a location, and not lastKnownLocation,so we won't receive null

}
