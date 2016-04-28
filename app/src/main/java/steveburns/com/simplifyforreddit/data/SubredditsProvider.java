package steveburns.com.simplifyforreddit.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.List;

/**
 * Created by sburns on 4/23/16.
 */
public class SubredditsProvider extends ContentProvider {
    private SQLiteOpenHelper mOpenHelper;

    interface Tables {
        String SUBREDDITS = "subreddits";
    }

    private static final int ITEMS = 0;
    private static final int ITEMS__ID = 1;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SubredditsContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "items", ITEMS);
        matcher.addURI(authority, "items/#", ITEMS__ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SubredditsDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return SubredditsContract.Subreddits.CONTENT_TYPE;
            case ITEMS__ID:
                return SubredditsContract.Subreddits.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        Cursor cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS: {
                final long _id = db.insertOrThrow(Tables.SUBREDDITS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return SubredditsContract.Subreddits.buildItemUri(_id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).delete(db);
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();
        switch (match) {
            case ITEMS: {
                return builder.table(Tables.SUBREDDITS);
            }
            case ITEMS__ID: {
                final String _id = paths.get(1);
                return builder.table(Tables.SUBREDDITS).where(SubredditsContract.Subreddits._ID + "=?", _id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }
}
