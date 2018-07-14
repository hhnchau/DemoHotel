package com.appromobile.hotel.db.search;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by xuanquach on 7/22/15.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "appro_hotel.db";
    private static final int DATABASE_VERSION = 2;


    DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableHelper.SearchHistory.QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TableHelper.SearchHistory.QUERY_DROP_TABLE);
        onCreate(db);
    }
}
