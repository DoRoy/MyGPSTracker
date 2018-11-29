package mygpstracker.android.mygpstracker;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by doroy on 18-Jul-18.
 * The location MyLog, saving the location and the date on a file.
 * implemented as Singleton.
 */

class MyLog {

    private static MyLog ourInstance;
    private static ContentResolver contentResolver;
    private File file;

    public void setFile(File file) {
        synchronized (file) {
            this.file = file;
        }
    }

    public void setResolver(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }



    static MyLog getInstance() {
        if(ourInstance == null) {
            ourInstance = new MyLog();
        }
        return ourInstance;
    }

    private MyLog() {
    }


    public void writeWithResolver(String date, String latitude, String longitude){
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.COLUMN_DATE, date);
        values.put(MyContentProvider.COLUMN_LONGITUDE, latitude);
        values.put(MyContentProvider.COLUMN_LATITUDE, longitude);
        contentResolver.insert(MyContentProvider.CONTENT_URI, values);
    }

    public String readWithResolver(){
        String ans = "";
        Cursor cursor = contentResolver.query(MyContentProvider.CONTENT_URI,null,null,null,null);

        while(cursor.moveToNext()){
            //String s1 = cursor.getString(MyContentProvider.LOCATIONS_ID);
            String s2 = cursor.getString(MyContentProvider.LOCATIONS_DATE);
            String s3 = cursor.getString(MyContentProvider.LOCATIONS_LONGITUDE);
            String s4 = cursor.getString(MyContentProvider.LOCATIONS_LATITUDE);

            ans += s2 + ": LAT = " + s3 + ", LON = " + s4 + "\n";
        }
        cursor.close();
        return ans;
    }

    public void resetDataBase(){
        contentResolver.delete(MyContentProvider.CONTENT_URI,null,null);
    }


    public void writeSettingsUpdate(String date, String latitude, String longitude){
        writeWithResolver("\t** " + date, latitude, longitude + " **");
    }



}

