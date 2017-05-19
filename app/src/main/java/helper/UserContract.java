package helper;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Ronan-Local on 18/05/2017.
 */

public class UserContract {
    //Uri for ContentProvider
    public static final String CONTENT_AUTHORITY = "com.example.ronan_local.findit";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USER = "user";

    /* Inner class that defines the table contents */
    public static class User_Table implements BaseColumns {
        //Uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();
        public static final String CONTENT_TYPE_USER = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_USER;
        //table and column names
        public static final String TABLE_NAME = "USER_TABLE";
        public static final String KEY_ID = "uID";
        public static final String KEY_EMAIL = "email";

        //public static final String COLUMN_MOVIE = "POPULAR_MOVIE";

        public static Uri buildMovieUriWithID(long ID){
            return ContentUris.withAppendedId(CONTENT_URI,ID);
        }
    }
}
