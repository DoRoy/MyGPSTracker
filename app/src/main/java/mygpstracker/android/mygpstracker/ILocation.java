package mygpstracker.android.mygpstracker;

import android.location.Location;

/**
 * Created by doroy on 18-Jul-18.
 * Defines the interface of a class that can monitor a location
 */

public interface ILocation {
    Location getLastKnownLocation();

    /**
     * @return: array format - {meanLatitude, meanLongitude}
     */
    double[] getMeanLocation(Location[] locations);
}

