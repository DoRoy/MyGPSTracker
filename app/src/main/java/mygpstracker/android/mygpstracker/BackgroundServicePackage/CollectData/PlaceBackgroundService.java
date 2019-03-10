package mygpstracker.android.mygpstracker.BackgroundServicePackage.CollectData;

import android.util.ArrayMap;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import mygpstracker.android.mygpstracker.BackgroundServicePackage.ABackgroundService;
import mygpstracker.android.mygpstracker.Places.MyPlaces;

public class PlaceBackgroundService extends ABackgroundService {

    private MyPlaces myPlaces;

    @Override
    public void onCreate() {
        myPlaces = new MyPlaces(getApplicationContext());
        setPeriod(getDoubleInHours(0.25));
        super.onCreate();
    }

    @Override
    protected Class<? extends ABackgroundService> getClassChild() {
        return this.getClass();
    }

    @Override
    protected List<TimerTask> getTimerTask() {
        ArrayList<TimerTask> list = new ArrayList<>();
        list.add(new TimerTask() {
            @Override
            public void run() {
                try {
                    ArrayMap<Place, Float> data = myPlaces.guessCurrentPlace();
                    sqliteHelper.createVisitedPlace(data.keyAt(0), data.get(data.keyAt(0)));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return list;
    }
}
