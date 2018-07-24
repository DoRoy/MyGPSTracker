package mygpstracker.android.mygpstracker;

/**
 * Created by doroy on 24-Jul-18.
 */

public class SamplePolicy {
    private int timesToTakeLocation;
    private double intervalsInMinutes;

    public SamplePolicy(int timesToTakeLocation, double intervalsInMinutes) {
        this.timesToTakeLocation = timesToTakeLocation;
        this.intervalsInMinutes = intervalsInMinutes;
    }

    public int getTimesToTakeLocation() {
        return timesToTakeLocation;
    }

    public double getIntervalsInMinutes() {
        return intervalsInMinutes;
    }
}
