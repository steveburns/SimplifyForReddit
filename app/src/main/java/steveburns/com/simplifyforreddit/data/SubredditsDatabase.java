package steveburns.com.simplifyforreddit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static steveburns.com.simplifyforreddit.data.SubredditsProvider.Tables;

/**
 * Created by sburns on 4/23/16.
 */
public class SubredditsDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "simplifyforreddit.db";
    private static final int DATABASE_VERSION = 1;

    public SubredditsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.SUBREDDITS + " ("
                + SubredditsContract.SubredditsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SubredditsContract.SubredditsColumns.NAME + " TEXT NOT NULL"
//                + SubredditsContract.SubredditsColumns.DESCRIPTION + " TEXT"
                + ")" );
        
        // Insert pre-installed list of subreddits
        db.execSQL("INSERT INTO " + Tables.SUBREDDITS + "(" + SubredditsContract.SubredditsColumns.NAME + ")" +
                " VALUES('android')," +
                "('art')," +
                "('askscience')," +
                "('books')," +
                "('diy')," +
                "('documentaries')," +
                "('fitness')," +
                "('food')," +
                "('futurology')," +
                "('gadgets')," +
                "('gaming')," +
                "('getmotivated')," +
                "('history')," +
                "('ios')," +
                "('lifeprotips')," +
                "('mildlyinteresting')," +
                "('mobile')," +
                "('movies')," +
                "('music')," +
                "('news')," +
                "('personalfinance')," +
                "('philosophy')," +
                "('photoshopbattles')," +
                "('science')," +
                "('space')," +
                "('sports')," +
                "('worldnews')"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.SUBREDDITS);
        onCreate(db);
    }
}
