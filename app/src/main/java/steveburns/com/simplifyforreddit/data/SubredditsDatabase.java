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
        /* Subreddits */
        db.execSQL("CREATE TABLE " + Tables.SUBREDDITS + " ("
                + SubredditsContract.SubredditsColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SubredditsContract.SubredditsColumns.NAME + " TEXT NOT NULL"
                + ")" );

        /* Submissions */
        db.execSQL("CREATE TABLE " + Tables.SUBMISSIONS + " ("
                + SubredditsContract.SubmissionColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SubredditsContract.SubmissionColumns.SUBREDDIT_NAME + " TEXT NOT NULL, "
                + SubredditsContract.SubmissionColumns.TITLE + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.AUTHOR + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.CREATE_DATE_UTC + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.SELF_TEXT + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.URL + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.THUMBNAIL + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.THUMBNAIL_TYPE + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.POST_HINT + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.PERMA_LINK + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.COMMENT_COUNT + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.COMMENT_1 + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.COMMENT_2 + " TEXT NULL, "
                + SubredditsContract.SubmissionColumns.COMMENT_3 + " TEXT NULL "
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
