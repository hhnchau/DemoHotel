package com.appromobile.hotel.db.search;

/**
 * Created by xuanquach on 7/22/15.
 */
public class TableHelper {
    public static class SearchHistory {

        public static final String TABLE_NAME = "search_history";

        public static final String ID = "_id";
        public static final String TEXT_SEARCH = "_text_search";


        public static String [] PROJECT_ALL =new String[]{
            ID, TEXT_SEARCH
        };

        public static String QUERY_CREATE_TABLE="";
        public final static String QUERY_DROP_TABLE;
        static {
            StringBuilder builder = new StringBuilder();
            builder.append("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+ " ");
            builder.append("(");
            builder.append(ID +" INTEGER PRIMARY KEY AUTOINCREMENT, ");
            builder.append(TEXT_SEARCH+" TEXT ");
            builder.append(")");
            QUERY_CREATE_TABLE = builder.toString();
            QUERY_DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
        }
    }

}
