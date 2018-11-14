package mygpstracker.android.mygpstracker.GPSTesting;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mygpstracker.android.mygpstracker.ILocation;


/**
 * Created by doroy on 10-Oct-18.
 * Inspired by https://github.com/jaisonfdo/LocationHelper
 */

public class GPSTest extends Activity implements ILocation, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ActivityCompat.OnRequestPermissionsResultCallback, PermissionUtils.PermissionResultCallback {

    // LogCat tag
    private static final String TAG = "GPSTest";

    private final static int PLAY_SERVICES_REQUEST = 1000;
    private final static int REQUEST_CHECK_SETTINGS = 2000;

    private Location mLastLocation;

    // Google client to interact with Google API

    private GoogleApiClient mGoogleApiClient;

    double latitude;
    double longitude;

    // list of permissions

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    boolean isPermissionGranted;

    Context context;

    public GPSTest(Context context) {
        this.context = context;
        permissionUtils=new PermissionUtils(context,this);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);

        // check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }



    @Override
    public Location getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation");
        //getLocation();
        if (mLastLocation != null) {
            Log.d(TAG, "getLastKnownLocation - mLastLocation is NOT NULL");
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();


        } else {
            Log.d(TAG, "getLastKnownLocation - mLastLocation is NULL");
/*            if(btnProceed.isEnabled())
                btnProceed.setEnabled(false);

            showToast("Couldn't get the location. Make sure location is enabled on the device");*/
        }
        return mLastLocation;
    }

    public void getPeriodicLocation(int interval, int fastestInterval, int priority){
        // https://www.programcreek.com/java-api-examples/index.php?api=com.google.android.gms.location.LocationRequest

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(priority);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
    }

    @Override
    public double[] getMeanLocation(Location[] locations) {
        return new double[0];
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Once connected with google api, get the location
        Log.d(TAG, "onConnected");
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    // Permission check functions


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }




    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
        isPermissionGranted=true;
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

    public void showToast(String message)
    {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    /**
     * Method to display the location on UI
     * */

    private void getLocation() {
        Log.d(TAG,"GetLocation");
        if (isPermissionGranted) {

            try
            {
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                if (mLastLocation == null)
                    Log.d(TAG,"GetLocation - mLastLocation is NULL");
                else{
                    Log.d(TAG,"Location worked!!");
                    Log.d(TAG,"\t LAT: " + mLastLocation.getLatitude());
                    Log.d(TAG,"\t LON: " + mLastLocation.getLongitude());
                }
            }
            catch (SecurityException e)
            {
                e.printStackTrace();
            }

        }

    }

    public Address getAddress(double latitude, double longitude)
    {
        Log.d(TAG,"getAddress(double latitude, double longitude)");
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }



    public Address getLastAddress() {
        Log.d(TAG, "getLastAddress()");
        Address locationAddress = getAddress(latitude, longitude);

        return locationAddress;
    }

    public String getAddressString(Address locationAddress){
        String currentLocation = "";
        if(locationAddress!=null)
        {
            Log.d(TAG,"getAddressString(Address locationAddress) - address is not null");
            int i = 0;
            int lines = locationAddress.getMaxAddressLineIndex();
            String tempAdderss = "\n";
            while(i < lines){
                tempAdderss += locationAddress.getAddressLine(i) + "\n";
                i++;
            }
            if(locationAddress.getFeatureName() != null)
                tempAdderss += "FeatureName: " + locationAddress.getFeatureName() + "\n";
            if(locationAddress.getLocality() != null)
                tempAdderss += "Locality: " + locationAddress.getLocality() + "\n";
            if(locationAddress.getPremises() != null)
                tempAdderss += "Premises: " + locationAddress.getPremises() + "\n";
            if(locationAddress.getPhone() != null)
                tempAdderss += "Phone: " + locationAddress.getPhone() + "\n";
            if(locationAddress.getUrl() != null)
                tempAdderss += "Url: " + locationAddress.getUrl() + "\n";
            if(locationAddress.getExtras() != null) {
                Bundle bundle = locationAddress.getExtras();
                tempAdderss += "Extras: " + bundle.toString();
            }
            locationAddress.getThoroughfare();
            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();


            if(!TextUtils.isEmpty(address))
            {
                currentLocation=address;

                if (!TextUtils.isEmpty(address1))
                    currentLocation+="\n"+address1;

                if (!TextUtils.isEmpty(city))
                {
                    currentLocation+="\n"+city;

                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+=" - "+postalCode;
                }
                else
                {
                    if (!TextUtils.isEmpty(postalCode))
                        currentLocation+="\n"+postalCode;
                }

                if (!TextUtils.isEmpty(state))
                    currentLocation+="\n"+state;

                if (!TextUtils.isEmpty(country))
                    currentLocation+="\n"+country;

            }
            currentLocation += tempAdderss;

        }
        return currentLocation;
    }

    public String getAddressString(){
        Log.d(TAG,"getAddressString()");
        Address locationAddress = getAddress(latitude,longitude);
        return getAddressString(locationAddress);
    }

    /**
     * Creating google api client object
     * */

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG,"buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here
                        getLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(GPSTest.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });


    }




    /**
     * Method to verify google play services on the device
     * */

    private boolean checkPlayServices() {
        Log.d(TAG,"checkPlayServices");

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this,resultCode,
                        PLAY_SERVICES_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    //TODO - try to make periodic updates work, or try to work with GPSTest as the regular location provider

    // TODO - maybe we need to create a locationCallBack that has GPSTest in it and make it periodic with this

}
