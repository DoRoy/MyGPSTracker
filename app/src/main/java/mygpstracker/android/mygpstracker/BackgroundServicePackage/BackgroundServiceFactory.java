package mygpstracker.android.mygpstracker.BackgroundServicePackage;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData.BatteryInfoBackgroundService;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData.PlaceBackgroundService;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData.SensorBackgroundService;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.SendData.SendTablesBackgroundService;

public class BackgroundServiceFactory {

    public static ArrayList<Class> getAllServicesClass(){
        ArrayList<Class> classList = new ArrayList<>();

        classList.add(BatteryInfoBackgroundService.class);
        classList.add(PlaceBackgroundService.class);
        classList.add(SendTablesBackgroundService.class);
        classList.add(SensorBackgroundService.class);

        return classList;
    }
}
