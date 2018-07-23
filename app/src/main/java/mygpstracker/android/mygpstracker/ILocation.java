package mygpstracker.android.mygpstracker;

import android.location.Location;

/**
 * Created by doroy on 18-Jul-18.
 */

public interface ILocation {
    void getLocation();
    Location getLastLocation();

    /**
     * @return: array format - {meanLantitude, meanLongtitude}
     */
    double[] getMeanLocation(Location[] locations);
}

