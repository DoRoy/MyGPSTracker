package mygpstracker.android.mygpstracker.RecyclerViewTest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by doroy on 14-Oct-18.
 */

class RecyclerLocationLog {

    //TODO - make this table fucking work

    private static RecyclerLocationLog ourInstance;
    private static ContentResolver contentResolver;


    public void setResolver(ContentResolver contentResolver){
        this.contentResolver = contentResolver;
    }


    static RecyclerLocationLog getInstance() {
        if(ourInstance == null) {
            ourInstance = new RecyclerLocationLog();
        }
        return ourInstance;
    }

    private RecyclerLocationLog() {
    }


    public void writeWithResolver(String name, String oldRating, String newRating){
        ContentValues values = new ContentValues();
        values.put(RecyclerLocationContentProvider.COLUMN_NAME, name);
        values.put(RecyclerLocationContentProvider.COLUMN_OLD_RATING, oldRating);
        values.put(RecyclerLocationContentProvider.COLUMN_NEW_RATING, newRating);
        contentResolver.insert(RecyclerLocationContentProvider.CONTENT_URI, values);
    }

    public List<String[]> readWithResolver(){
        ArrayList<String[]> list = new ArrayList();
        Cursor cursor = contentResolver.query(RecyclerLocationContentProvider.CONTENT_URI,null,null,null,null);
        if(cursor != null) {
            while ( cursor.moveToNext()) {
                //String s1 = cursor.getString(MyContentProvider.LOCATIONS_ID);
                String s2 = cursor.getString(RecyclerLocationContentProvider.LOCATIONS_NAME);
                String s3 = cursor.getString(RecyclerLocationContentProvider.LOCATIONS_LONGITUDE);
                String s4 = cursor.getString(RecyclerLocationContentProvider.LOCATIONS_LATITUDE);

                String[] temp = {s2, s3, s4};
                list.add(temp);
            }
            cursor.close();
        }
        return list;
    }

    public void resetDataBase(){
        contentResolver.delete(RecyclerLocationContentProvider.CONTENT_URI,null,null);
    }


    public void writeSettingsUpdate(String date, String latitude, String longitude){
        writeWithResolver("\t** " + date, latitude, longitude + " **");
    }
}
