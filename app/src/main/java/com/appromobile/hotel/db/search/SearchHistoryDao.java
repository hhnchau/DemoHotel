package com.appromobile.hotel.db.search;

import android.content.ContentValues;
import android.database.Cursor;

import com.appromobile.hotel.model.view.SearchHistoryEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuanquach on 9/3/15.
 */
public class SearchHistoryDao {
    public SearchHistoryEntry parseEntry(Cursor cursor) {
        try {
            SearchHistoryEntry entry = new SearchHistoryEntry();
            entry.setId(cursor.getInt(cursor.getColumnIndex(TableHelper.SearchHistory.ID)));
            entry.setText(cursor.getString(cursor.getColumnIndex(TableHelper.SearchHistory.TEXT_SEARCH)));
            return entry;
        } catch (Exception e) {
            return null;
        }
    }

    public long insertDownload(String textSearch) {
        try {
            ContentValues val = new ContentValues();
            val.put(TableHelper.SearchHistory.TEXT_SEARCH, textSearch);
            long id = DatabaseConnection.database.insert(TableHelper.SearchHistory.TABLE_NAME, null, val);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getCountTextSearch(String textSearch) {
        try {
            Cursor cursor = DatabaseConnection.database.query(TableHelper.SearchHistory.TABLE_NAME, TableHelper.SearchHistory.PROJECT_ALL,
                    TableHelper.SearchHistory.TEXT_SEARCH + " = " + textSearch, null, null, null, TableHelper.SearchHistory.ID + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getCount();
            }

        } catch (Exception e) {

        }
        return 0;
    }

    public List<SearchHistoryEntry> getListAll() {
        try {
            List<SearchHistoryEntry> entries = new ArrayList<>();
            Cursor cursor = DatabaseConnection.database.query(TableHelper.SearchHistory.TABLE_NAME, TableHelper.SearchHistory.PROJECT_ALL,
                    null, null, null, null, TableHelper.SearchHistory.ID + " DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    entries.add(parseEntry(cursor));
                } while (cursor.moveToNext());

                if (entries != null && entries.size() > 0)
                    return entries;
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteItem(int id) {
        try {
            int deleteStatus = DatabaseConnection.database.delete(TableHelper.SearchHistory.TABLE_NAME, TableHelper.SearchHistory.ID + " = " + Integer.toString(id), null);
            System.out.println("deleteStatus: " + deleteStatus);
        } catch (Exception e) {
        }
    }
}
