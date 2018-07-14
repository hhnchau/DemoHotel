package com.appromobile.hotel.db.search;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by appro on 21/03/2018.
 */

public class PopupCenterDao extends SQLiteOpenHelper {
    private static PopupCenterDao Instance = null;

    public static PopupCenterDao getInstance(Context context) {
        if (Instance == null) {
            Instance = new PopupCenterDao(context);
        }
        return Instance;
    }

    private static final String DATABASE_NAME = "POPUP_CENTERSSSSSSS";
    private static final int DATABASE_VERSION = 1;
    private final static String TABLE = "tbl_popup";
    private final static String FIELD_SN = "sn";
    private final static String FIELD_VIEW = "view";

    private PopupCenterDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + TABLE + "(" + FIELD_SN + " INTEGER PRIMARY KEY," + FIELD_VIEW + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void insert(PopupSql popupSql) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FIELD_SN, popupSql.getSn());
            values.put(FIELD_VIEW, popupSql.getView());
            db.insert(TABLE, null, values);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(PopupSql popupSql) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FIELD_SN, popupSql.getSn());
            values.put(FIELD_VIEW, popupSql.getView());
            db.update(TABLE, values, FIELD_SN + " = ?", new String[]{String.valueOf(popupSql.getSn())});
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(int sn) {
        String selectQuery = "UPDATE " + TABLE + " SET view = view + 1 WHERE sn = " + sn ;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(selectQuery);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int sn) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE, FIELD_SN + " = ?", new String[]{String.valueOf(sn)});
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PopupSql> selectAll() {
        List<PopupSql> list = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = null;

        String selectQuery = "SELECT  * FROM " + TABLE;// + " ORDER BY " + FIELD_SN + " DESC";

        try {
            db = this.getWritableDatabase();

            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    list.add(new PopupSql(
                            Integer.parseInt(cursor.getString(0)),
                            Integer.parseInt(cursor.getString(1))));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return list;
    }


}
