package com.appromobile.hotel.db.search;

import android.database.sqlite.SQLiteDatabase;

import com.appromobile.hotel.HotelApplication;

public class DatabaseConnection {

	private static DataBaseHelper dbHelper;
	static SQLiteDatabase database;

	public static void createDBInstance() {
        if (dbHelper == null && database == null) {
            dbHelper = new DataBaseHelper(HotelApplication.getContext());
            database = dbHelper.getWritableDatabase();
        }
	}

    public static void closeDbConnection(){
        try {
            if(dbHelper!=null){
                dbHelper.close();
                dbHelper=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}