package mygpstracker.android.mygpstracker;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MyContentProvider extends ContentProvider {

    // fields for my content provider
    public static final String PROVIDER_NAME = "mygpstracker.android.mygpstracker.Location";
    public static final String URL = "content://" + PROVIDER_NAME + "/MyContentProvider";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    // fields for the database
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // integer values used in content URI
    static final int LOCATIONS = 5;
    static final int LOCATIONS_ID = 0;
    static final int LOCATIONS_DATE = 1;
    static final int LOCATIONS_LONGITUDE = 2;
    static final int LOCATIONS_LATITUDE = 3;


    private DBHelper dbHelper;

    // projection map for a query
    private static HashMap<String, String> BirthMap;

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "MyContentProvider", LOCATIONS);
        uriMatcher.addURI(PROVIDER_NAME, "MyContentProvider/#", LOCATIONS_ID);
        uriMatcher.addURI(PROVIDER_NAME, "MyContentProvider/#",LOCATIONS_DATE);
        uriMatcher.addURI(PROVIDER_NAME, "MyContentProvider/#",LOCATIONS_LONGITUDE);
        uriMatcher.addURI(PROVIDER_NAME, "MyContentProvider/#",LOCATIONS_LATITUDE);
    }

    // database declarations
    private SQLiteDatabase database;
    public static final String DATABASE_NAME = "MyGPSTrackerDataBase";
    public static final String TABLE_NAME = "MyGPSTrackerTable";
    public static final int DATABASE_VERSION = 1;
    public static final String CREATE_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " date TEXT NOT NULL, " +
                    " longitude TEXT NOT NULL, " +
                    " latitude TEXT NOT NULL);";


    // class that creates and manages the provider's database
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
            onCreate(db);
        }

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);

        // permissions to be writable
        database = dbHelper.getWritableDatabase();

        if(database == null)
            return false;
        else
            return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // the TABLE_NAME to query on
        queryBuilder.setTables(TABLE_NAME);
        int match = uriMatcher.match(uri);
        switch (match) {
            // maps all database column names
            case LOCATIONS:
                queryBuilder.setProjectionMap(BirthMap);
                break;
            case LOCATIONS_ID:
                queryBuilder.appendWhere( COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case LOCATIONS_DATE:
                queryBuilder.appendWhere( COLUMN_DATE + "=" + uri.getLastPathSegment());
                break;
            case LOCATIONS_LONGITUDE:
                queryBuilder.appendWhere( COLUMN_LONGITUDE + "=" + uri.getLastPathSegment());
                break;
            case LOCATIONS_LATITUDE:
                queryBuilder.appendWhere( COLUMN_LATITUDE + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            // No sorting-> sort on names by default
            sortOrder = COLUMN_DATE;
        }
        Cursor cursor = queryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);


        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = database.insert(TABLE_NAME, "", values);

        // If record is added successfully
        if(row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Fail to add a new record into " + uri);
    }

    public void reset(){
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LOCATIONS:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                count = database.update(TABLE_NAME, values, COLUMN_ID +
                        " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case LOCATIONS:
                // delete all the records of the table
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATIONS_ID:
                String id = uri.getLastPathSegment();	//gets the id
                count = database.delete( TABLE_NAME, COLUMN_ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;


    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            // Get all friend-birthday records
            case LOCATIONS:
                return "vnd.android.cursor.dir/vnd.example.friends";
            // Get a particular friend
            case LOCATIONS_ID:
                return "vnd.android.cursor.item/vnd.example.friends";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


    public static void main(String[] args) {

        String URL = "content://com.javacodegeeks.provider.Birthday/friends";
        MyContentProvider myContentProvider = new MyContentProvider();
        ContentValues values = new ContentValues();
        values.put(myContentProvider.COLUMN_DATE, "today");
        values.put(myContentProvider.COLUMN_LONGITUDE, "123");
        values.put(myContentProvider.COLUMN_LATITUDE, "345");
        ContentValues values2 = new ContentValues();
        values2.put(myContentProvider.COLUMN_DATE, "tommarrow");
        values2.put(myContentProvider.COLUMN_LONGITUDE, "345");
        values2.put(myContentProvider.COLUMN_LATITUDE, "567");
        myContentProvider.insert(myContentProvider.CONTENT_URI,values);
        myContentProvider.insert(myContentProvider.CONTENT_URI,values2);
        Cursor cursor = myContentProvider.query(myContentProvider.CONTENT_URI,null,null,null,myContentProvider.COLUMN_DATE);
        System.out.println(cursor.getColumnIndex(myContentProvider.COLUMN_DATE));
    }
}

