package mygpstracker.android.mygpstracker;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by doroy on 24-Jul-18.
 */

public class SamplePolicy {
    private static AtomicInteger timesToTakeLocation = new AtomicInteger();
    private volatile static Double intervalsInMinutes;

    public SamplePolicy(int timesToTakeLocation, double intervalsInMinutes) {
        this.timesToTakeLocation.set(timesToTakeLocation);
        this.intervalsInMinutes = new Double(intervalsInMinutes);
    }

    public static int getTimesToTakeLocation() {
        return timesToTakeLocation.get();
    }

    public static double getIntervalsInMinutes() {
        double ans = 0;
        synchronized (intervalsInMinutes){
            ans = intervalsInMinutes;
        }
        return ans;
    }

    public static void setTimesToTakeLocation(int newTimesToTakeLocation) {
        timesToTakeLocation.set(newTimesToTakeLocation);
    }

    public static void setIntervalsInMinutes(double newIntervalsInMinutes) {
        synchronized (intervalsInMinutes){
            intervalsInMinutes = intervalsInMinutes;
        }

    }
}
