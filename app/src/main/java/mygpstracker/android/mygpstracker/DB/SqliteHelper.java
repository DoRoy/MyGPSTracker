package mygpstracker.android.mygpstracker.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import mygpstracker.android.mygpstracker.Battery.BatteryInfoWrapper;

public class SqliteHelper extends SQLiteOpenHelper {

    // Logcat TAG
    private static final String TAG = SqliteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MyDB";

    // Table Names
    public static final String TABLE_PLACES = "places";
    public static final String TABLE_VISITED_PLACES = "visited_places";
    public static final String TABLE_SENSORS = "sensors";
    public static final String TABLE_BATTERY = "battery";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_CREATED_AT = "created_at";

    //PLACES Table - Column names
    private static final String KEY_PLACE = "place_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_RATE = "rate";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PRICE = "price";
    private static final String KEY_URI = "uri";


    // BATTERY Table - Columns name
    private static final String KEY_CHARGING_TYPE = "charging_type";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_SCALE = "scale";
    private static final String KEY_PERCENT = "percent";
    private static final String KEY_TEMPERATURE = "temperature";
    private static final String KEY_VOLTAGE = "voltage";
    private static final String KEY_HEALTH = "health";
    private static final String KEY_CAPACITY = "capacity";
    private static final String KEY_TECHNOLOGY = "technology";

    // SENSORS Table - Columns name
    private static final String KEY_SENSOR_NAME = "sensor_name";
    private static final String KEY_RECORD = "record";
    private static final String KEY_MAX_VALUE = "max_value";
    private static final String KEY_MIN_VALUE = "min_value";
    private static final String KEY_MEAN_VALUE = "mean_value";
    private static final String KEY_MEDIAN_VALUE = "median_value";
    private static final String KEY_DEVIATION_VALUE = "deviation_value";
    private static final String KEY_POWER_VALUE = "power_value";
    private static final String KEY_MAX_RANGE_VALUE = "maxRange_value";



    // VISITED_PLACES - Columns name
    private static final String KEY_PLACE_ID = "place_id";
    private static final String KEY_LIKELIHOOD = "likelihood";

    //Table Create Statements
    // Places table create statement
    private static final String CREATE_TABLE_PLACES = "CREATE TABLE "+ TABLE_PLACES
                                                    + "(" + KEY_PLACE + " TEXT PRIMARY KEY ,"
                                                    +       KEY_NAME + " TEXT,"
                                                    +       KEY_LAT + " DOUBLE,"
                                                    +       KEY_LON + " DOUBLE,"
                                                    +       KEY_ADDRESS + " TEXT,"
                                                    +       KEY_RATE + " FLOAT,"
                                                    +       KEY_PHONE + " TEXT,"
                                                    +       KEY_PRICE + " INTEGER,"
                                                    +       KEY_URI + " TEXT,"
                                                    +       KEY_CREATED_AT + " DATETIME" + ");";

     // Battery table create statement
    private static final String CREATE_TABLE_BATTERY = "CREATE TABLE "+ TABLE_BATTERY
                                                    + "(" + KEY_CREATED_AT + " DATETIME PRIMARY KEY,"
                                                    +       KEY_LEVEL + " INTEGER,"
                                                    +       KEY_CHARGING_TYPE + " TEXT,"
                                                    +       KEY_SCALE + " INTEGER,"
                                                    +       KEY_PERCENT + " INTEGER,"
                                                    +       KEY_TEMPERATURE + " FLOAT,"
                                                    +       KEY_VOLTAGE + " INTEGER,"
                                                    +       KEY_HEALTH + " TEXT,"
                                                    +       KEY_CAPACITY + " INTEGER,"
                                                    +       KEY_TECHNOLOGY + " TEXT);";

    // Visited_Places table create statement
    private static final String CREATE_TABLE_VISITED_PLACES = "CREATE TABLE "+ TABLE_VISITED_PLACES
                                                    + "(" + KEY_PLACE_ID + " TEXT,"
                                                    +       KEY_LIKELIHOOD + " FLOAT,"
                                                    +       KEY_CREATED_AT + " DATETIME,"
                                                    +       "PRIMARY KEY (" + KEY_CREATED_AT + "," + KEY_PLACE_ID + "));";


    // Battery table create statement
    private static final String CREATE_TABLE_SENSORS = "CREATE TABLE "+ TABLE_SENSORS
            + "(" + KEY_CREATED_AT + " DATETIME,"
            +       KEY_SENSOR_NAME + " TEXT,"
            +       KEY_RECORD + " TEXT,"
            +       "PRIMARY KEY (" + KEY_CREATED_AT + "," + KEY_SENSOR_NAME + "));";
/*
    private static final String CREATE_TABLE_SENSORS = "CREATE TABLE "+ TABLE_SENSORS
            + "(" + KEY_CREATED_AT + " DATETIME,"
            +       KEY_SENSOR_NAME + " TEXT,"
            +       KEY_MAX_VALUE + " TEXT,"
            +       KEY_MIN_VALUE  + " TEXT,"
            +       KEY_MEAN_VALUE + " TEXT,"
            +       KEY_MEDIAN_VALUE  + " TEXT,"
            +       KEY_DEVIATION_VALUE + " TEXT,"
            +       KEY_POWER_VALUE  + " TEXT,"
            +       KEY_MAX_RANGE_VALUE + " TEXT,"
            +       "PRIMARY KEY (" + KEY_CREATED_AT + "," + KEY_SENSOR_NAME + "));";
*/



    public SqliteHelper(Context context){
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
//        SPARSE_ARRAY = SensorFactory.getAllAvailableSensorsNameAndID();

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_PLACES);
        db.execSQL(CREATE_TABLE_BATTERY);
        db.execSQL(CREATE_TABLE_VISITED_PLACES);
        db.execSQL(CREATE_TABLE_SENSORS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        deleteTables(db);

        // create new tables
        onCreate(db);
        db.setVersion(newVersion);
    }

    private void deleteTables(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITED_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        deleteTables(db);
        onCreate(db);
        db.close();
    }

    public void resetTable(String tableName){
        if (checkTableName(tableName)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + tableName);
        }
    }

    private boolean checkTableName(String tableName){
        String[] tablesNames = getAllTablesNames();
        for (String table: tablesNames) {
            if (table.equals(tableName))
                return true;
        }
        return false;
    }

    public String[] getAllTablesNames(){
        return new String[]{TABLE_BATTERY, TABLE_PLACES, TABLE_VISITED_PLACES , TABLE_SENSORS};
    }

    /**
     * Create a string formatted as csv file from the given table name
     * @param tableName
     * @return
     */
    public String getCSVString(String tableName){
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            CSVBuilder csvBuilder = new CSVBuilder();

            // add the columns names at the beginning of the file
            String[] columnNames = cursor.getColumnNames();
            csvBuilder.writeNext(columnNames);

            int columnCount = cursor.getColumnCount();
            boolean emptyTable = true;
            // Add all the records in the table.
            while (cursor.moveToNext()) {
                emptyTable = false;
                String[] record = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    record[i] = cursor.getString(i);
                }
                csvBuilder.writeNext(record);
            }
            cursor.close();
            db.close();

            // If the table is empty return null so we wont send empty files, otherwise return the csv String
            return ((emptyTable) ? null : csvBuilder.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (db != null && db.isOpen()) db.close();
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return null;
    }



    // -------------------- Sensors Table Methods -------------------- //

    public long createSensorRecord(String sensorName, String record){
        //TODO - figure out how to build this table with all the different data types
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_NAME, sensorName);
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_RECORD, record);
        // insert row
        long placeNumber = 0;
        try {
            placeNumber = db.insertOrThrow(TABLE_SENSORS, null, values);
        }catch (SQLException e){
//            e.printStackTrace();
        }
        finally {
            if (db.isOpen()) db.close();
        }

        return placeNumber;
    }

















    // -------------------- Places Table Methods -------------------- //

    /**
     * Creating a new place
     * @param place
     * @return number of items in places table
     */
    public long createPlace(Place place){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE, place.getId());
        values.put(KEY_NAME, place.getName().toString());
        values.put(KEY_LAT, place.getLatLng().latitude);
        values.put(KEY_LON, place.getLatLng().longitude);
        try{
            values.put(KEY_ADDRESS, Objects.requireNonNull(place.getAddress()).toString());
        }catch(NullPointerException e){
            values.put(KEY_ADDRESS, (Byte) null);
        }
        values.put(KEY_RATE, place.getRating());
        try{
            values.put(KEY_PHONE, Objects.requireNonNull(place.getPhoneNumber()).toString());
        }catch(NullPointerException e){
            values.put(KEY_PHONE, (Byte) null);
        }
        values.put(KEY_PRICE, place.getPriceLevel());
        try {
            values.put(KEY_URI, Objects.requireNonNull(place.getWebsiteUri()).toString());
        }catch(NullPointerException e){
            values.put(KEY_URI, (Byte) null);
        }
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long placeNumber = 0;
        try {
            placeNumber = db.insertOrThrow(TABLE_PLACES, null, values);
        }catch (SQLException e){
//            e.printStackTrace();
        }
        finally {
            if (db.isOpen()) db.close();
        }

        return placeNumber;
    }

    /**
     * get a place by ID
     * @param place_id
     * @return
     */
/*    public Place getPlaceByID(int place_id){
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PLACES + " WHERE "
                              + KEY_PLACE + " = " + place_id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

    }*/


    /**
     * Updating a Place
     */
    public int updatePlace(Place place) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE, place.getId());
        values.put(KEY_NAME, place.getName().toString());
        values.put(KEY_LAT, place.getLatLng().latitude);
        values.put(KEY_LON, place.getLatLng().longitude);
        values.put(KEY_ADDRESS, place.getAddress().toString());
        values.put(KEY_RATE, place.getRating());
        values.put(KEY_PHONE, place.getPhoneNumber().toString());
        values.put(KEY_PRICE, place.getPriceLevel());
        values.put(KEY_URI, place.getWebsiteUri().toString());
        values.put(KEY_CREATED_AT, getDateTime());

        // updating row
        return db.update(TABLE_PLACES, values, KEY_PLACE + " = ?",
                new String[] { String.valueOf(place.getId()) });
    }


    /**
     * Deleting a place
     */
    public void deletePlaceByID(long place_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLACES, KEY_PLACE + " = ?",
                new String[] { String.valueOf(place_id) });
    }


    // -------------------- Visited-Places Table Methods -------------------- //

    /**
     * This method receive a place, insert the place to the places, and to the visited_places
     * @param place
     * @param likelihood
     * @return
     */
    public long createVisitedPlace(Place place, float likelihood) {
        createPlace(place);
        return createVisitedPlace(place.getId(), likelihood);

    }


        /**
         * Creating visited_place
         */
    public long createVisitedPlace(String place_id, float likelihood) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_ID, place_id);
        values.put(KEY_LIKELIHOOD, likelihood);
        values.put(KEY_CREATED_AT, getDateTime());
        long id = 0;
        try {
            id = db.insertOrThrow(TABLE_VISITED_PLACES, null, values);
        }catch (SQLException e){
//            e.printStackTrace();
        }
        closeDB();
        return id;
    }



    // -------------------- Battery Table Methods -------------------- //

    public long createBatteryInfo(BatteryInfoWrapper batteryInfoWrapper){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CHARGING_TYPE, batteryInfoWrapper.getAttribute(KEY_CHARGING_TYPE));
        values.put(KEY_LEVEL, batteryInfoWrapper.getAttribute(KEY_LEVEL));
        values.put(KEY_SCALE, batteryInfoWrapper.getAttribute(KEY_SCALE));
        values.put(KEY_PERCENT, batteryInfoWrapper.getAttribute(KEY_PERCENT));
        values.put(KEY_TEMPERATURE, batteryInfoWrapper.getAttribute(KEY_TEMPERATURE));
        values.put(KEY_VOLTAGE, batteryInfoWrapper.getAttribute(KEY_VOLTAGE));
        values.put(KEY_HEALTH , batteryInfoWrapper.getAttribute(KEY_HEALTH));
        values.put(KEY_CAPACITY , batteryInfoWrapper.getAttribute(KEY_CAPACITY));
        values.put(KEY_TECHNOLOGY , batteryInfoWrapper.getAttribute(KEY_TECHNOLOGY));
        values.put(KEY_CREATED_AT, getDateTime());

        long id = db.insert(TABLE_BATTERY, null,values);
        closeDB();
        return id;
    }


    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }


    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }





}


