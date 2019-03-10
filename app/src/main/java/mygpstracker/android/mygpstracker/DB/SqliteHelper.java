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
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public static final String TABLE_CALLS = "calls";
    public static final String TABLE_NETWORK = "network";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_ACTIVITY = "activities";

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
/*
    private static final String KEY_MAX_VALUE = "max_value";
    private static final String KEY_MIN_VALUE = "min_value";
    private static final String KEY_MEAN_VALUE = "mean_value";
    private static final String KEY_MEDIAN_VALUE = "median_value";
    private static final String KEY_DEVIATION_VALUE = "deviation_value";
    private static final String KEY_POWER_VALUE = "power_value";
    private static final String KEY_MAX_RANGE_VALUE = "maxRange_value";
*/

    // VISITED_PLACES - Columns name
    private static final String KEY_PLACE_ID = "place_id";
    private static final String KEY_LIKELIHOOD = "likelihood";

    // Calls Table - Columns name
    private static final String KEY_CALLS_COMMITTED = "calls_committed";
    private static final String KEY_CALLS_UNCOMMITTED = "calls_uncommitted";
    private static final String KEY_MAX_VALUE = "max_value";
    private static final String KEY_MIN_VALUE = "min_value";
    private static final String KEY_SUM_DURATION = "mean_value";
    private static final String KEY_MEDIAN_VALUE = "median_value";
    private static final String KEY_DEVIATION_VALUE = "deviation_value";

    // NETWORK Table - Columns names
    private static final String KEY_TYPE = "type";
    private static final String KEY_SPEED = "speed";

    // REVIEWS Table - Columns names
    private static final String KEY_REVIEW = "review";
    private static final String KEY_DATE_OF_VISIT = "date_of_visit";
    private static final String KEY_COMPANION = "companion";
    private static final String KEY_VISITING_FREQUENCY = "visiting_frequency";
    private static final String KEY_VISITING_PURPOSE = "visiting_purpose";

    // ACTIVITY Table - Columns names
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_CONFIDENCE = "confidence";


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


    // Call Information table create statement
    private static final String CREATE_TABLE_CALLS = "CREATE TABLE "+ TABLE_CALLS
        + "(" + KEY_CREATED_AT + " DATETIME PRIMARY KEY,"
        +       KEY_CALLS_UNCOMMITTED + " TEXT,"
        +       KEY_CALLS_COMMITTED + " TEXT,"
        +       KEY_MAX_VALUE + " TEXT,"
        +       KEY_MIN_VALUE  + " TEXT,"
        +       KEY_SUM_DURATION + " TEXT,"
        +       KEY_MEDIAN_VALUE  + " TEXT,"
        +       KEY_DEVIATION_VALUE + " TEXT);";

    // Network Information table create statement
    private static final String CREATE_TABLE_NETWORK = "CREATE TABLE "+ TABLE_NETWORK
            + "(" + KEY_CREATED_AT + " DATETIME PRIMARY KEY,"
            +       KEY_TYPE + " TEXT,"
            +       KEY_SPEED + " TEXT);";

    // Reviews table create statement
    private static final String CREATE_TABLE_REVIEWS = "CREATE TABLE "+ TABLE_REVIEWS
            + "(" + KEY_PLACE_ID + " TEXT,"
            +       KEY_CREATED_AT + " DATETIME,"
            +       KEY_REVIEW + " TEXT,"
            +       KEY_DATE_OF_VISIT + " TEXT,"
            +       KEY_COMPANION + " TEXT,"
            +       KEY_VISITING_FREQUENCY + " TEXT,"
            +       KEY_VISITING_PURPOSE + " TEXT,"
            +       KEY_RATE + " FLOAT,"
            +       "PRIMARY KEY (" + KEY_CREATED_AT + "," + KEY_PLACE_ID + "));";

    // ACTIVITY Information table create statement
    private static final String CREATE_TABLE_ACTIVITY = "CREATE TABLE "+ TABLE_ACTIVITY
            + "(" + KEY_CREATED_AT + " DATETIME PRIMARY KEY,"
            +       KEY_ACTIVITY + " TEXT);";
//            +       KEY_CONFIDENCE + " TEXT);";


    private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

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
        db.execSQL(CREATE_TABLE_CALLS);
//        db.execSQL(CREATE_TABLE_NETWORK);
        db.execSQL(CREATE_TABLE_REVIEWS);
        db.execSQL(CREATE_TABLE_ACTIVITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        deleteTables(db);

        // create new tables
        onCreate(db);
        db.setVersion(newVersion);
    }

    /**
     * Delete all the tables.
     * @param db
     */
    private void deleteTables(SQLiteDatabase db){
        String[] tablesNames = this.getAllTablesNames();
        for (String table: tablesNames) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }
/*        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITED_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NETWORK);*/
    }


    private long writeContent(ContentValues values, String tableName){
        readWriteLock.writeLock().lock();
        SQLiteDatabase db = this.getWritableDatabase();
        long returnedValue = -1;
        try {
            returnedValue = db.insertOrThrow(tableName, null, values);

        }catch (SQLException e){
//            e.printStackTrace();
        }
        finally {
            if (db.isOpen()) db.close();
            db.close();
            readWriteLock.writeLock().unlock();
        }
        return returnedValue;
    }

    /**
     * Delete and recreate the tables.
     * The method updates the table if there was a change in the table format.
     */
    public void resetTables(){
        readWriteLock.writeLock().lock();
        SQLiteDatabase db = getWritableDatabase();
        deleteTables(db);
        onCreate(db);
        db.close();
        readWriteLock.writeLock().unlock();
    }

    /**
     * Reset a specific table.
     * @param tableName - The table name
     * @return  - True if worked, False otherwise
     */
    public boolean resetTable(String tableName){
        if (checkTableName(tableName)) {
            readWriteLock.writeLock().lock();
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM " + tableName);
            db.close();
            readWriteLock.writeLock().unlock();
            return true;
        }
        return false;
    }

    /**
     * Checks if the table name exist in the scope of this class.
     * @param tableName
     * @return
     */
    private boolean checkTableName(String tableName){
        String[] tablesNames = getAllTablesNames();
        for (String table: tablesNames) {
            if (table.equals(tableName))
                return true;
        }
        return false;
    }

    /**
     * Gets all the existing tables names
     * @return
     */
    public String[] getAllTablesNames(){
        return new String[]{TABLE_BATTERY, TABLE_PLACES, TABLE_VISITED_PLACES , TABLE_SENSORS, TABLE_CALLS, TABLE_REVIEWS, TABLE_ACTIVITY};
    }

    /**
     * Create a string formatted as csv file from the given table name
     * @param tableName - A name of the an Existing table, can be found as public static attributes.
     * @return  - A String of all the data in the table, null if the tableName doesn't exist.
     */
    public String getCSVString(String tableName){
        if (!checkTableName(tableName))
            return null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            readWriteLock.readLock().lock();
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
            readWriteLock.readLock().unlock();

            // If the table is empty return null so we wont send empty files, otherwise return the csv String
            return ((emptyTable) ? null : csvBuilder.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (db != null && db.isOpen()) db.close();
            if (cursor != null && !cursor.isClosed()) cursor.close();
            readWriteLock.readLock().unlock();
        }

        return null;
    }



    // -------------------- Sensors Table Methods -------------------- //

    /**
     * Add a record to Sensors table
     * @param sensorName    - The name of the sensor
     * @param record    - A String representing the data
     * @return
     */
    public long createSensorRecord(String sensorName, String record){

        ContentValues values = new ContentValues();
        values.put(KEY_SENSOR_NAME, sensorName);
        values.put(KEY_CREATED_AT, getDateTime());
        values.put(KEY_RECORD, record);
        // insert row
        long placeNumber = writeContent(values,TABLE_SENSORS);
/*        try {
            placeNumber = db.insertOrThrow(TABLE_SENSORS, null, values);
        }catch (SQLException e){
//            e.printStackTrace();
        }
        finally {
            if (db.isOpen()) db.close();
        }*/

        return placeNumber;
    }


    // -------------------- Places Table Methods -------------------- //

    /**
     * Creating a new place
     * @param place
     * @return number of items in places table
     */
    public long createPlace(Place place){
        //SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = createContentValueForPlaces(place);
        // insert row
        long placeNumber = writeContent(values,TABLE_PLACES);
/*        try {
            placeNumber = db.insertOrThrow(TABLE_PLACES, null, values);
        }catch (SQLException e){
//            e.printStackTrace();
        }
        finally {
            if (db.isOpen()) db.close();
        }*/

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

        ContentValues values = createContentValueForPlaces(place);

        // updating row
        return db.update(TABLE_PLACES, values, KEY_PLACE + " = ?",
                new String[] { String.valueOf(place.getId()) });
    }

    private ContentValues createContentValueForPlaces(Place place){
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
        return values;
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
     * This method receives a place, insert the place to the places, and to the visited_places
     * @param place
     * @param likelihood
     * @return
     */
    public long createVisitedPlace(Place place, float likelihood) {
        createPlace(place);
        return createVisitedPlace(place.getId(), likelihood);

    }


    /**
     * Create a new visited place record.
     * @param place_id  - The Place's ID.
     * @param likelihood    - The likelihood that the person is in the place
     * @return  - the long returned from the db.insert method.
     * */
    public long createVisitedPlace(String place_id, float likelihood) {
        //SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_ID, place_id);
        values.put(KEY_LIKELIHOOD, likelihood);
        values.put(KEY_CREATED_AT, getDateTime());
        long id = writeContent(values, TABLE_VISITED_PLACES);
/*        try {
            id = db.insertOrThrow(TABLE_VISITED_PLACES, null, values);
        }catch (SQLException e){
//            e.printStackTrace();
        }
        closeDB();*/
        return id;
    }



    // -------------------- Battery Table Methods -------------------- //

    /**
     *  Receive a BatteryInfoWrapper and insert it to the Battery table
     * @param batteryInfoWrapper  - A wrapper that wraps the information we wish to insert to the table
     * @return  - the long returned from the db.insert method.
     */
    public long createBatteryInfo(BatteryInfoWrapper batteryInfoWrapper){
        //SQLiteDatabase db = this.getWritableDatabase();
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
//        long id = db.insert(TABLE_BATTERY, null, values);
        long id = writeContent(values,TABLE_BATTERY);
        //closeDB();
        return id;
    }

    // -------------------- Calls Table Methods -------------------- //

    /**
     *  Receive a CallsInfoWrapper and insert it to the Calls table
     * @param callsInfoWrapper  - A wrapper that wraps the information we wish to insert to the table
     * @return  - the long returned from the db.insert method.
     */
    public long createCallsRecord(CallsInfoWrapper callsInfoWrapper){
        // get a writable db
        //SQLiteDatabase db = this.getWritableDatabase();
        // create the content value we wish to insert
        ContentValues values = new ContentValues();
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_CALLS_UNCOMMITTED,callsInfoWrapper.getUncommitted());
        values.put(KEY_CALLS_COMMITTED ,callsInfoWrapper.getCommitted());
        values.put(KEY_MAX_VALUE , callsInfoWrapper.getMax());
        values.put(KEY_MIN_VALUE ,  callsInfoWrapper.getMin());
        values.put(KEY_SUM_DURATION , callsInfoWrapper.getDuration());
        values.put(KEY_MEDIAN_VALUE ,callsInfoWrapper.getMedian());
        values.put(KEY_DEVIATION_VALUE ,callsInfoWrapper.getDeviation());
        // insert the data to the DB
        //long id = db.insert(TABLE_CALLS, null,values);
        long id = writeContent(values, TABLE_CALLS);
//        closeDB();
        return id;

    }



    // -------------------- Network Table Methods -------------------- //

    public long createNetworkRecord(String type, String speed){
        ContentValues values = new ContentValues();
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_TYPE,type);
        values.put(KEY_SPEED,speed);

        return writeContent(values, TABLE_NETWORK);
    }


    // -------------------- Reviews Table Methods -------------------- //

    public long createReviewRecord(ReviewInfoWrapper reviewInfoWrapper){
        ContentValues values = new ContentValues();
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_PLACE_ID,reviewInfoWrapper.getID());
        values.put(KEY_REVIEW,reviewInfoWrapper.getReview());
        values.put(KEY_DATE_OF_VISIT,reviewInfoWrapper.getDateOfVisit());
        values.put(KEY_COMPANION,reviewInfoWrapper.getCompanion());
        values.put(KEY_VISITING_FREQUENCY,reviewInfoWrapper.getFrequency());
        values.put(KEY_VISITING_PURPOSE,reviewInfoWrapper.getPurpose());
        values.put(KEY_RATE,reviewInfoWrapper.getRate());

        return writeContent(values, TABLE_REVIEWS);
    }

    // -------------------- Activity Table Methods -------------------- //

    public long createActivityRecord(String activity){
        ContentValues values = new ContentValues();
        values.put(KEY_CREATED_AT,getDateTime());
        values.put(KEY_ACTIVITY, activity);
//        values.put(KEY_CONFIDENCE, confidence);

        return writeContent(values, TABLE_ACTIVITY);
    }


    // -------------------- General Methods -------------------- //


    /**
     * Closes the DB in case it is open
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * Receives a table name and return the last time-stamp of inserted data.
     * @param tableName
     * @return  - A string of the last timestamp in the table, null if the table is empty or the table name doesn't exist.
     */
    public String getLastTimeStamp(String tableName) {
        if (checkTableName(tableName)) {
            readWriteLock.readLock().lock();
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT " + KEY_CREATED_AT + " FROM " + tableName + " ORDER BY " + KEY_CREATED_AT + " DESC LIMIT 1";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToNext()) {
                int index = cursor.getColumnIndex(KEY_CREATED_AT);
                String timeCreated = cursor.getString(index);
                cursor.close();
                db.close();
                readWriteLock.readLock().unlock();
                return timeCreated;
            }
            if (!cursor.isClosed()) cursor.close();
            if (db.isOpen())db.close();
            readWriteLock.readLock().unlock();

        }
        return null;
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


