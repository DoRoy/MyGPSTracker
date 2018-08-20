package mygpstracker.android.mygpstracker;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by doroy on 24-Jul-18.
 */

public class SamplePolicy {
    private AtomicInteger timesToTakeLocation = new AtomicInteger();
    private volatile static Double intervalsInMinutes;

    public SamplePolicy(int timesToTakeLocation, double intervalsInMinutes) {
        this.timesToTakeLocation.set(timesToTakeLocation);
        this.intervalsInMinutes = new Double(intervalsInMinutes);
    }

    public int getTimesToTakeLocation() {
        return timesToTakeLocation.get();
    }

    public double getIntervalsInMinutes() {
        double ans = 0;
        synchronized (this.intervalsInMinutes){
            ans = intervalsInMinutes;
        }
        return ans;
    }

    public void setTimesToTakeLocation(int timesToTakeLocation) {
        this.timesToTakeLocation.set(timesToTakeLocation);
    }

    public void setIntervalsInMinutes(double intervalsInMinutes) {
        synchronized (this.intervalsInMinutes){
            this.intervalsInMinutes = intervalsInMinutes;
        }

    }
}
