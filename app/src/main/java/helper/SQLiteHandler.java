package helper;

/*
 * Created by tmoodey on 13/05/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "c773androidMySQL";

    public SQLiteDatabase myDB;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myDB = getWritableDatabase();

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        myDB = db;
        String CREATE_FRIEND_TABLE = "CREATE TABLE IF NOT EXISTS " + FriendsContract.Friends_Table.TABLE_NAME + "("
                + FriendsContract.Friends_Table.KEY_ID + " INTEGER PRIMARY KEY," + FriendsContract.Friends_Table.KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_FRIEND_TABLE);

        db.execSQL(CREATE_FRIEND_TABLE);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FriendsContract.Friends_Table.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void clearTable(String table_name)
    {
        myDB.execSQL("DELETE FROM "+ table_name);
    }

}
