package mygpstracker.android.mygpstracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.ashokvarma.sqlitemanager.SqliteDataRetriever;

//TODO - try to make this work https://android-arsenal.com/details/1/7021

public class HelperSqliteDataRetriever implements SqliteDataRetriever {
    SQLiteOpenHelper mSqliteHelper;
    SQLiteDatabase mSQLiteDatabase;

    HelperSqliteDataRetriever(SQLiteOpenHelper sqliteHelper) {
        mSqliteHelper = sqliteHelper;
        mSQLiteDatabase = mSqliteHelper.getWritableDatabase();
    }

    @Override
    public Cursor rawQuery(@NonNull String query, String[] selectionArgs) {
        if (mSQLiteDatabase == null || !mSQLiteDatabase.isOpen()) {
            mSQLiteDatabase = mSqliteHelper.getWritableDatabase();
        }
        return mSQLiteDatabase.rawQuery(query, selectionArgs);
    }

    @Override
    public String getDatabaseName() {
        return mSqliteHelper.getDatabaseName();
    }

    @Override
    public void freeResources() {
        // not good practice to open multiple database connections and close every time
    }
}
