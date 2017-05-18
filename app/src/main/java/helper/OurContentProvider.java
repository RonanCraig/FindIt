package helper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Ronan-Local on 18/05/2017.
 */

public class OurContentProvider extends ContentProvider {

    public static final String LOG_TAG = "OurContentProvider";
    private static final UriMatcher myUriMatcher = buildUriMatcher();
    public static SQLiteHandler myDBHelper;
    public static final int FRIEND = 100;
    public static final int USER = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(FriendsContract.CONTENT_AUTHORITY,FriendsContract.PATH_FRIEND,FRIEND);
        matcher.addURI(UserContract.CONTENT_AUTHORITY,UserContract.PATH_USER,USER);

        //matcher.addURI(MovieContract.CONTENT_AUTHORITY,MovieContract.PATH_MOVIE+"/#",MOVIE_WITH_ID);

        return matcher;
    }

    public OurContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int match_code = myUriMatcher.match(uri);

        switch(match_code){
            case FRIEND:{
                myDBHelper.clearTable(FriendsContract.Friends_Table.TABLE_NAME);
                break;
            }
            case USER:{
                myDBHelper.clearTable(UserContract.User_Table.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int match_code = myUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case FRIEND:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(FriendsContract.Friends_Table.TABLE_NAME,null,values);
                if (_id > 0) {
                    retUri = FriendsContract.Friends_Table.buildMovieUriWithID(_id);
                }
                else
                    throw new SQLException("failed to insert");
                break;
            }
            case USER:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(UserContract.User_Table.TABLE_NAME,null,values);
                if (_id > 0) {
                    retUri = UserContract.User_Table.buildMovieUriWithID(_id);
                }
                else
                    throw new SQLException("failed to insert");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        Log.i(LOG_TAG, "insert success");

        return retUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDBHelper = new SQLiteHandler(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        int match_code = myUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code) {
            case FRIEND: {
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        FriendsContract.Friends_Table.TABLE_NAME, // Table to Query
                        projection,//Columns
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null // sort order
                );
                Log.i(LOG_TAG, "querying for FRIEND");
                Log.i(LOG_TAG, myCursor.getCount() + "");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        myCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return myCursor;
    }
}
