package mygpstracker.android.mygpstracker;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by doroy on 18-Jul-18.
 */
public class GPSLocation implements ILocation {


    private Semaphore semaphore;
    LocationManager mLocationManager;
    AppCompatActivity application;
    private FusedLocationProviderClient mFusedLocationClient;
    Location lastLocation;

    public GPSLocation(AppCompatActivity application) {
        this.application = application;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(application);
        semaphore = new Semaphore(1);
    }

    public Location getLastKnownLocation(){
        boolean fineLocation = ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean coarseLocation = ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;

        if (fineLocation && coarseLocation) {
            ActivityCompat.requestPermissions(application, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
        Task<Location> task = mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //semaphore.release();
            }
        });
/*        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        while(!task.isComplete());
        Location ll = task.getResult();
        return ll;
    }



    @Override
    /**
     * @return: array format - {meanLantitude, meanLongtitude}
     */
    public double[] getMeanLocation(Location[] locations) {
        //todo implement
        double meanLon = 0, meanLan = 0;

        meanLan = meanLatitude(locations);
        meanLon = meanLongtitude(locations);
        double[] ans = {meanLan, meanLon};
        return ans;
    }

/*    private double meanLongtitude(Location[] locations) {
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


    private double meanLongtitude(Location[] locations){
        double meanLong = 0;
        for(int i = 0; i < locations.length; i ++){
            meanLong += locations[i].getLongitude();
        }
        meanLong = meanLong / locations.length;
        return meanLong;

    }
    private double meanLatitude(Location[] locations){
        double meanLan = 0;
        for(int i = 0; i < locations.length; i ++){
            meanLan += locations[i].getLatitude();
        }
        meanLan = meanLan / locations.length;
        return meanLan;
    }


}
