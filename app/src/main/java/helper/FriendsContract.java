package helper;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ronan-Local on 18/05/2017.
 */

public class FriendsContract {
    //Uri for ContentProvider
    public static final String CONTENT_AUTHORITY = "com.example.ronan_local.findit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FRIEND = "friend";

    /* Inner class that defines the table contents */
    public static class Friends_Table implements BaseColumns {
        //Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIEND).build();
        public static final String CONTENT_TYPE_FRIEND = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_FRIEND;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_FRIEND;
        //table and column names
        public static final String TABLE_NAME = "FRIEND_TABLE";
        public static final String KEY_ID = "uID";
        public static final String KEY_NAME = "name";

        //public static final String COLUMN_MOVIE = "POPULAR_MOVIE";

        public static Uri buildMovieUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
}
