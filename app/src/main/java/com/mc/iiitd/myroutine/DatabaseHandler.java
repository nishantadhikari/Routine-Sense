package com.mc.iiitd.myroutine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nishant on 2/11/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "RankManager";

    // Contacts table name
    private static final String TABLE_RANK = "rank";

    // Contacts Table Columns names
    private static final String RANK1 = "rankl";
    private static final String KEY_NAME = "name";
    private static final String KEY_MAIL = "email";
    private static final String RANK2 = "rankg";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_RANK + "("
                + RANK1 + " INTEGER," + KEY_NAME + " TEXT,"+KEY_MAIL + " TEXT,"
                + RANK2 + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RANK);

        // Create tables again
        onCreate(db);
    }
    public void addRank(CustomClass rv) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(RANK1, rv.getCount());
        values.put(KEY_NAME, rv.getTitle());
        values.put(KEY_MAIL, rv.getSubtitle());
        values.put(RANK2, rv.getRank());
        db.insert(TABLE_RANK, null, values);
        db.close(); // Closing database connection
    }
    public void remove_all(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_RANK);
        db.close();
    }
    public List<CustomClass> getAllstored() {
        List<CustomClass> rankList = new ArrayList<CustomClass>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RANK;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CustomClass rv = new CustomClass(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2),Integer.parseInt(cursor.getString(3)));
                rankList.add(rv);
            } while (cursor.moveToNext());
        }

        // return contact list
        return rankList;
    }
}