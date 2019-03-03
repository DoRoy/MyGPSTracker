package mygpstracker.android.mygpstracker.Places;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mygpstracker.android.mygpstracker.AppExecutors;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.LocationSettingsBroadcastReceiver;


/**
 * Created by doroy on 18-Oct-18.
 */

public class MyPlaces extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "MyPlaces";
    public GoogleApiClient mGoogleApiClient;
    public GeoDataClient mGeoDataClient;
    Context context;
    public ArrayMap<Place,Float> places;
    private PlaceArrayAdapter mPlaceArrayAdapter;

    //https://www.programcreek.com/java-api-examples/index.php?project_name=windy1/google-places-api-java#
    //GooglePlaces googlePlaces;


    public MyPlaces(Context context) {
        this.context = context;
        if(CheckGooglePlayServices())
            buildGoogleApiClient();
        places = new ArrayMap<>();
    }

    public void setPlaceArrayAdapter(PlaceArrayAdapter mPlaceArrayAdapter) {
        this.mPlaceArrayAdapter = mPlaceArrayAdapter;
        if(CheckGooglePlayServices())
            buildGoogleApiClient();
    }

    private boolean CheckGooglePlayServices() {
        Log.d(TAG, "CheckGooglePlayServices");
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();

        mGeoDataClient = Places.getGeoDataClient(context);
    }





    public ArrayMap<Place,Float> guessCurrentPlace() {
        Log.d(TAG, "guessCurrentPlace");
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "guessCurrentPlace - Connected");
            @SuppressLint("MissingPermission") PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    AppExecutors.getInstance().networkIO().execute(() -> {
                        setPlacesList(likelyPlaces);
                    });

                }
            });
            try {
                synchronized (places) {
                    places.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        else{
            Log.d(TAG, "guessCurrentPlace - Not Connected");
            buildGoogleApiClient();
        }
        return places;
    }

    private void setPlacesList(PlaceLikelihoodBuffer likelyPlaces) {
        Log.d(TAG, "setPlacesList");

        synchronized (places) {
            int size = likelyPlaces.getCount();
            for(int i = 0; i < size; i++){
                PlaceLikelihood placeLikelihood = likelyPlaces.get(i);
                places.put(placeLikelihood.getPlace(),placeLikelihood.getLikelihood());
            }
            places.notifyAll();
        }
    }



    List<Place> placeByIDList = new LinkedList<>();

    public List<Place> getPlaceByID(String IDString){
        Log.d(TAG, "getPlaceByID");
        final Task<PlaceBufferResponse> placeResponse = mGeoDataClient.getPlaceById(IDString);
        placeResponse.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                Log.d(TAG, "getPlaceByID - onComplete");

                placeByIDList.clear();
                PlaceBufferResponse placeBufferResponse = task.getResult();
                new Thread(() -> {
                   setPlaceByID(placeBufferResponse);
                }).start();
            }
        });
        try {
            synchronized (placeByIDList) {
                placeByIDList.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return placeByIDList;
    }

    private void setPlaceByID(PlaceBufferResponse placeBufferResponse) {
        Log.d(TAG,"setPlaceByID");
        synchronized (placeByIDList){
            int size = placeBufferResponse.getCount();
            for (int i = 0; i < size; i++){
                placeByIDList.add(placeBufferResponse.get(i).freeze());
            }
            placeBufferResponse.release();
            placeByIDList.notifyAll();
        }
    }

    /*
    get photos, this site helps: http://www.zoftino.com/google-places-api-photos-android
     */

    List<PlacePhotoMetadata> photosDataList = new ArrayList<>();

    private void setPhotosMetadataList(PlacePhotoMetadataResponse photos){
        Log.d(TAG, "setPhotosMetadataList");
        synchronized (photosDataList) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();

            Log.d(TAG, "number of photos " + photoMetadataBuffer.getCount());

            for (PlacePhotoMetadata photoMetadata : photoMetadataBuffer) {
                photosDataList.add(photoMetadata.freeze());
            }

            photoMetadataBuffer.release();
            photosDataList.notifyAll();
        }

    }

    public List<PlacePhotoMetadata> getPhotoMetadata(String placeID){
        Log.d(TAG, "getPhotoMetadata");
        final Task<PlacePhotoMetadataResponse> photoResponse = mGeoDataClient.getPlacePhotos(placeID);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                        int currentPhotoIndex = 0;
                        photosDataList.clear();
                        PlacePhotoMetadataResponse photos = task.getResult();
                        AppExecutors.getInstance().networkIO().execute(() -> {
                            setPhotosMetadataList(photos);
                        });

                    }
                });
        try {
            synchronized (photosDataList) {
                photosDataList.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return photosDataList;
    }

    Bitmap photoBitmap;
    Object lock = new Object();

    private void setBitmapPhoto(PlacePhotoResponse photo){
        Log.d(TAG, "setBitmapPhoto");
        synchronized (lock){
            photoBitmap = photo.getBitmap();
            lock.notifyAll();
        }

    }

    public Bitmap getPhotoBitmap(PlacePhotoMetadata photoMetadata){
        Log.d(TAG, "getPhotoBitmap");
        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                PlacePhotoResponse photo = task.getResult();
                AppExecutors.getInstance().networkIO().execute(() -> {
                    setBitmapPhoto(photo);
                });

                /*placeImage.invalidate();
                placeImage.setImageBitmap(photoBitmap);*/
            }
        });
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return photoBitmap;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(mPlaceArrayAdapter != null)
            mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(mPlaceArrayAdapter != null)
            mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }
}
