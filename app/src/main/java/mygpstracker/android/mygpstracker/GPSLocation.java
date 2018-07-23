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


/**
 * Created by doroy on 18-Jul-18.
 */
public class GPSLocation implements ILocation {

    Location lastLocation;
    double latitude;
    double longitude;
    LocationManager locationManager;
    AppCompatActivity application;

    public GPSLocation(AppCompatActivity application){
        locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        this.application = application;
    }


    public void getLocation() {


        if (ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(application, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (isLocationEnabled()) {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER,  new LocationListener(){
                @Override
                public void onLocationChanged(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    lastLocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
        }

    }

    @Override
    public Location getLastLocation() {
        return lastLocation;
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

    private double meanLongtitude(Location[] locations) {
        double meanLon = 0, cosLon = 0, sinLon = 0;
        for(int i = 0; i < locations.length; i++){
            sinLon += Math.sin(locations[i].getLongitude());
            cosLon += Math.cos(locations[i].getLongitude());
        }
        sinLon = sinLon / locations.length;
        cosLon = cosLon / locations.length;
        meanLon = Math.atan2(sinLon,cosLon);
        return meanLon;
    }

    private double meanLatitude(Location[] locations){
        double meanLan = 0;
        for(int i = 0; i < locations.length; i ++){
            meanLan += locations[i].getLatitude();
        }
        meanLan = meanLan / locations.length;
        return meanLan;
    }


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
