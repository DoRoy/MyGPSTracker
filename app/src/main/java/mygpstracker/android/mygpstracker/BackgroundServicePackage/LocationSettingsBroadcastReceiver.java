package mygpstracker.android.mygpstracker.BackgroundServicePackage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import mygpstracker.android.mygpstracker.BackgroundService;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData.PlaceBackgroundService;

public class LocationSettingsBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    private static final String ACTION_GPS = "android.location.PROVIDERS_CHANGED";
    private static ReentrantLock lock = new ReentrantLock();
    private static AtomicBoolean isActive = new AtomicBoolean(false);





    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, " onReceive");
        System.out.println(" onReceive");
        if (intent != null) {
            String s = intent.getAction();
            System.out.println(s);
            if (s != null) {
                if (s.equals(ACTION_GPS)) {
                    checkGPS(context);
                }
            }
        }

    }

    private void checkGPS(Context context) {
        Log.d(TAG, " checkGPS");
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lock.lock();
            boolean b = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            locationManager = null;
            if (b && isActive.compareAndSet(false,true)) {
                Log.d(TAG, " checkGPS - GPS is on");
                System.out.println(" checkGPS - GPS is on");
                context.startService(new Intent(context, PlaceBackgroundService.class));

            } else if (!b && isActive.compareAndSet(true,false)){
                Log.d(TAG, " checkGPS - GPS is off");
                System.out.println(" checkGPS - GPS is off");
                context.stopService(new Intent(context, PlaceBackgroundService.class));
            }
            lock.unlock();
        }
    }

}
