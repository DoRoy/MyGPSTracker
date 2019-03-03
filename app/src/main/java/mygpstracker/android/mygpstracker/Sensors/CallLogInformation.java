package mygpstracker.android.mygpstracker.Sensors;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by doroy on 03-Sep-18.
 */

/***
 * String[] projection = new String[] {
    CallLog.Calls._ID,
    CallLog.Calls.NUMBER,
    CallLog.Calls.DATE,
    CallLog.Calls.DURATION,
    CallLog.Calls.TYPE
 };
    Filtering call Logs based on number, then use below code:
        String whereClause = CallLog.Calls.NUMBER + " = " + filterNumber;

        Cursor c =  mct.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? " ,
        new String[]{filterNumber}, CallLog.Calls.DATE + " DESC");
 */

public class CallLogInformation {

    ContentResolver managedQuery;

    public CallLogInformation(Context context) {

        managedQuery = context.getContentResolver();
    }

    public void printCallLog() {
        printCallLog(null, null, null, null);
    }


    public void printCallLog(String[] projection, String selection, String[] selectionArgs, String SortOrder) {
        StringBuffer sb = new StringBuffer();

        @SuppressLint("MissingPermission") Cursor managedCursor = managedQuery.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, SortOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        System.out.println(sb.toString());
    }

    /**
     * Parameters Order:
     * ans[0] - NUMBER
     * ans[1] - TYPE
     * ans[2] - DATE
     * ans[3] - DURATION
     */
    public List<String[]> query() {
        return query(null, null, null, null);
    }


    /**
     * Parameters Order:
     * ans[0] - NUMBER
     * ans[1] - TYPE
     * ans[2] - DATE
     * ans[3] - DURATION
     */
    public List<String[]> query(String[] projection, String selection, String[] selectionArgs, String SortOrder) {
        List<String[]> list = new ArrayList<>();
        @SuppressLint("MissingPermission") Cursor managedCursor = managedQuery.query(CallLog.Calls.CONTENT_URI, projection, selection, selectionArgs, SortOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            String[] tempString = new String[4];
            tempString[0] = managedCursor.getString(number);
            String callDate = managedCursor.getString(date);
            tempString[2] = new Date(Long.valueOf(callDate)).toString();
            tempString[3] = managedCursor.getString(duration);
            String callType = null;
            int dircode = Integer.parseInt(managedCursor.getString(type));
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callType = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callType = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callType = "MISSED";
                    break;
            }
            tempString[1] = callType;

            list.add(tempString);
        }
        managedCursor.close();

        return list;
    }

    /**
     * Gets only the durations of the calls.
     * @param timeStamp - If null return all the calls, otherwise gets only calls that were made after timeStamp
     * @return
     */
    public int[] getDurationsOnly(String timeStamp) {
        Cursor managedCursor = null;
        String selection = null;
        String[] selectionArgs = null;
        if (timeStamp != null) {
            selection = CallLog.Calls.DATE + " > ?";
            selectionArgs = new String[]{timeStamp };
        }
        else
            timeStamp = "";
        managedCursor = managedQuery.query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.DATE,CallLog.Calls.DURATION}, null, null,null );
        if (managedCursor != null) {
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            ArrayList<Integer> data = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            while (managedCursor.moveToNext()) {
                String dataString = managedCursor.getString(date);
                //dataString = new Date(Long.valueOf(dataString)).toString();
                dataString = dateFormat.format( new Date(Long.valueOf(dataString)));

                if (dataString.compareTo(timeStamp) > 0)
                    data.add(managedCursor.getInt(duration));
            }
            managedCursor.close();
            int[] array = new int[data.size()];
            for (int i = 0; i < array.length; i++) {
                array[i] = data.get(i);
            }
            return array;
        }
        return null;
    }
}
