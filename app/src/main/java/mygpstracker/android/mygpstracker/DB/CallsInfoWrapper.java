package mygpstracker.android.mygpstracker.DB;

import java.util.Arrays;

import mygpstracker.android.mygpstracker.Sensors.ASensorMeasures;

/**
 * A class wrapping the data of CallLog
 */
public class CallsInfoWrapper {

    private double mCommitted;
    private double mUncommitted;
    private double mDuration;
    private double mDeviation;
    private double mMax;
    private double mMin;
    private double mMedian;

    public CallsInfoWrapper(int[] data) throws Exception {
        double[] formattedData = getInfoFormat(data);
        setFromFormattedData(formattedData);
    }

    /**
     * A Setter for all the attributes
     * @param formattedData - An array formatted by 'getInfoFormat' method
     * @throws Exception    - If the argument given as 'formattedData' is null or if it isn't size 7
     */
    protected void setFromFormattedData(double[] formattedData) throws Exception {
        if (formattedData == null || formattedData.length != 7)
            throw new Exception("Formatted Data is null");
        mCommitted = formattedData[0];
        mUncommitted = formattedData[1];
        mDuration = formattedData[2];
        mDeviation = formattedData[3];
        mMax = formattedData[4];
        mMin = formattedData[5];
        mMedian = formattedData[6];
    }

    /**
     * Receives a data and return it in this order:
     * mCommitted - 0
     * mUncommitted - 1
     * mDuration - 2
     * mDeviation - 3
     * mMax - 4
     * mMin - 5
     * mMedian -6
     * @param data
     * @return
     */
    public static double[] getInfoFormat(int[] data) throws Exception {
        if (data == null || data.length < 1)
            throw new Exception("Data is null");
        int committed = 0;
        double duration = 0;
        double min = Double.MAX_VALUE;
        double max = 0;
        for (int i = 0; i < data.length; i++){
            if (data[i] != 0){
                duration += data[i];
                committed++;
                min = Math.min(min, data[i]);
                max = Math.max(max, data[i]);

            }
        }
        double mean = duration / committed;
        double sum = 0;
        for(int i = 0; i < data.length; i ++){
            if (data[i] != 0)
                sum += Math.pow(data[i] - mean, 2);
        }
        double deviation = Math.sqrt(sum / committed);

        Arrays.sort(data);
        double median = (double)data[(data.length - committed) + committed / 2];
        return new double[]{committed, data.length - committed, duration, ((deviation == Double.NaN)? 0: deviation), max, min, median};
    }

    public double getCommitted() {
        return mCommitted;
    }

    public double getUncommitted() {
        return mUncommitted;
    }

    public double getDuration() {
        return mDuration;
    }

    public double getDeviation() {
        return mDeviation;
    }

    public double getMax() {
        return mMax;
    }

    public double getMin() {
        return mMin;
    }

    public double getMedian() {
        return mMedian;
    }

}
