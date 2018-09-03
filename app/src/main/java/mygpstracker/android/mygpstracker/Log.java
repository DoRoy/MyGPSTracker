package mygpstracker.android.mygpstracker;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by doroy on 18-Jul-18.
 * The location Log, saving the location and the date on a file.
 * implemented as Singleton.
 */

class Log {

    private static Log ourInstance;
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



    static Log getInstance() {
        if(ourInstance == null) {
            ourInstance = new Log();
        }
        return ourInstance;
    }

    private Log() {
    }

    /**
     * Write a string to the log file
     * @param line - The line that will be written to the log.
     * @return - True if wrote the line, False if the were a problem and it didn't.
     */
    public boolean write(String line){
        synchronized (file) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            try {
                OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file, true));
                fileWriter.write(line);
                fileWriter.flush();
                fileWriter.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
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

    /**
     * Read the entire Log file content.
     * @return - A String containing all the log content.
     */
    public String read(){
        String ans = "";
        synchronized (file) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                String temp = null;
                temp = fileReader.readLine();
                while (temp != null) {
                    ans += temp + "\n";
                    temp = fileReader.readLine();
                }
                //System.out.println("read  - "+ ans);
                fileReader.close();
                return ans;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }




}

