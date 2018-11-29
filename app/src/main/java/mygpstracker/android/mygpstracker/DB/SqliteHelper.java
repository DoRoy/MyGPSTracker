package mygpstracker.android.mygpstracker.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;

import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mygpstracker.android.mygpstracker.Sensors.SensorFactory;

public class SqliteHelper extends SQLiteOpenHelper {

    // Logcat TAG
    private static final String TAG = SqliteHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "MyDB";

    // Table Names
    private static final String TABLE_PLACES = "places";
    private static final String TABLE_VISITED_PLACES = "visited_places";
    private static final String TABLE_SENSORS = "sensors";
    private static final String TABLE_BATTERY = "battery";

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
//    private final SparseArray SPARSE_ARRAY;

    // VISITED_PLACES - Columns name
    private static final String KEY_PLACE_ID = "place_id";
    private static final String KEY_LIKELIHOOD = "likelihood";

    //Table Create Statements
    // Places table create statement
    private static final String CREATE_TABLE_PLACES = "CREATE TABLE "+ TABLE_PLACES
                                                    + "(" + KEY_PLACE + " INTEGER PRIMARY KEY ,"
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
                                                    + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                    +       KEY_LEVEL + " INTEGER,"
                                                    +       KEY_CHARGING_TYPE + " TEXT,"
                                                    +       KEY_SCALE + " INTEGER,"
                                                    +       KEY_PERCENT + " INTEGER,"
                                                    +       KEY_TEMPERATURE + " FLOAT,"
                                                    +       KEY_VOLTAGE + " INTEGER,"
                                                    +       KEY_HEALTH + " TEXT,"
                                                    +       KEY_CAPACITY + " INTEGER,"
                                                    +       KEY_TECHNOLOGY + " TEXT,"
                                                    +       KEY_CREATED_AT + " DATETIME" + ");";

    // Visited_Places table create statement
    private static final String CREATE_TABLE_VISITED_PLACES = "CREATE TABLE "+ TABLE_VISITED_PLACES
                                                    + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                    +       KEY_PLACE_ID + " INTEGER,"
                                                    +       KEY_LIKELIHOOD + " FLOAT,"
                                                    +       KEY_CREATED_AT + " DATETIME,"
                                                    +       " FOREIGN KEY(" + KEY_PLACE_ID + ") REFERENCES " + TABLE_PLACES + "(" + KEY_PLACE + "));";


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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISITED_PLACES);

        // create new tables
        onCreate(db);

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
        values.put(KEY_ADDRESS, place.getAddress().toString());
        values.put(KEY_RATE, place.getRating());
        values.put(KEY_PHONE, place.getPhoneNumber().toString());
        values.put(KEY_PRICE, place.getPriceLevel());
        values.put(KEY_URI, place.getWebsiteUri().toString());
        values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long placeNumber = db.insert(TABLE_PLACES,null,values);

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
     * Creating todo_tag
     */
    public long createTodoTag(long place_id, float likelihood) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLACE_ID, place_id);
        values.put(KEY_LIKELIHOOD, likelihood);
        values.put(KEY_CREATED_AT, getDateTime());

        long id = db.insert(TABLE_VISITED_PLACES, null, values);

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
