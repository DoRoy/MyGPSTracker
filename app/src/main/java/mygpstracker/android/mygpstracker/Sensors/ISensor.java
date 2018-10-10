package mygpstracker.android.mygpstracker.Sensors;

import java.util.Map;

/**
 * Created by doroy on 29-Aug-18.
 */

public interface ISensor {

    Map<String,Double> getData();

    String getName();
}
