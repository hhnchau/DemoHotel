package com.appromobile.hotel.db.search;

/**
 * Created by xuanquach on 7/22/15.
 */
class TableHelper {
    public static class SearchHistory {

        static final String TABLE_NAME = "search_history";

        public static final String ID = "_id";
        static final String TEXT_SEARCH = "_text_search";


        static String[] PROJECT_ALL = new String[]{
                ID, TEXT_SEARCH
        };

        static String QUERY_CREATE_TABLE = "";
        final static String QUERY_DROP_TABLE;

        static {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ");
            builder.append("(");
            builder.append(ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
            builder.append(TEXT_SEARCH + " TEXT ");
            builder.append(")");
            QUERY_CREATE_TABLE = builder.toString();
            QUERY_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }
}
