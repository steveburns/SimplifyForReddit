package steveburns.com.simplifyforreddit.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sburns on 4/21/16.
 */
public class UpdaterService extends IntentService {
    private static final String TAG = UpdaterService.class.getSimpleName();

    // Get random post action
    public static final String ACTION_GET_RANDOM_POSTS = "steveburns.com.simplifyforreddit.intent.ACTION_GET_RANDOM_POSTS";
    public static final String EXTRA_NUM_RANDOM_POSTS = "num_random_posts";
    public static final Integer EXTRA_NUM_RANDOM_POSTS_DEFAULT_VALUE = 3;
    public static final String BROADCAST_ACTION_GET_RANDOM_POSTS = "steveburns.com.simplifyforreddit.intent.BROADCAST_GET_RANDOM_POSTS";

    // Get subreddit using name provided
    public static final String ACTION_GET_SUBREDDIT = "steveburns.com.simplifyforreddit.intent.ACTION_GET_SUBREDDIT";
    public static final String EXTRA_SUBREDDIT_NAME = "subreddit_name";
    public static final String EXTRA_SUBREDDIT_DESCRIPTION = "subreddit_desc";
    public static final String BROADCAST_ACTION_GET_SUBREDDIT = "steveburns.com.simplifyforreddit.intent.BROADCAST_GET_SUBREDDIT";

    public UpdaterService() {
        super(TAG);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null || !ni.isConnected()) {
            Log.w(TAG, "Not online, not refreshing.");
            return;
        }

        switch(intent.getAction()) {
            case ACTION_GET_RANDOM_POSTS:
                // setup result intent and broadcast
                int numPostsToGet = intent.getIntExtra(EXTRA_NUM_RANDOM_POSTS, EXTRA_NUM_RANDOM_POSTS_DEFAULT_VALUE);
                getRandomPosts(numPostsToGet);
                Intent postsIntent = new Intent(BROADCAST_ACTION_GET_RANDOM_POSTS);
                sendBroadcast(postsIntent);
                break;

            case ACTION_GET_SUBREDDIT:
                // Try to get a subreddit with the given name
                String requestedName = intent.getStringExtra(EXTRA_SUBREDDIT_NAME);
                Subreddit subreddit = RedditData.getSubreddit(requestedName);

                // setup result intent and broadcast
                Intent resultIntent = new Intent(BROADCAST_ACTION_GET_SUBREDDIT);
                if (subreddit != null) {
                    resultIntent.putExtra(EXTRA_SUBREDDIT_NAME, requestedName);
                    resultIntent.putExtra(EXTRA_SUBREDDIT_DESCRIPTION, subreddit.getPublicDescription());
                }
                sendBroadcast(resultIntent);
                break;
        }
    }

    private boolean getRandomPosts(int numToGet) {
        Cursor cursor = null;
        try {
            cursor = getApplicationContext()
                    .getContentResolver()
                    .query(SubredditsContract.Subreddits.buildDirUri(),
                            new String[]{SubredditsContract.Subreddits.NAME},
                            null,
                            null,
                            SubredditsContract.Subreddits.DEFAULT_SORT);
        } catch(Exception e) {
            Log.e(TAG, e.toString());
            return false;
        }

        if (cursor != null) {
            List<String> subreddits = new ArrayList<>();
            try {
                if(cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex(SubredditsContract.Subreddits.NAME));
                        if (!TextUtils.isEmpty(name)) {
                            subreddits.add(name);
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }

            // randomly select a number of subreddits
            numToGet = numToGet > subreddits.size() ? subreddits.size() : numToGet;
            for(int i = 0; i < numToGet; i++) {
                double randomValue = Math.random();
                int pos = (int)Math.round (randomValue * (subreddits.size()-1));
                if (pos >= 0 && pos < subreddits.size()) {
                    Log.d(TAG, String.format("Ramdomly selected subreddit: %s", subreddits.get(pos)));
                    storeRandomSubmissionForSubreddit(subreddits.get(pos));
                }
            }
        }
        return true;
    }

    /**
     * Queries for a random post in the given subreddit.
     * @param subredditName - name of the subreddit
     * @return result
     */
    private boolean storeRandomSubmissionForSubreddit(String subredditName) {

        Submission randomSubmission = RedditData.getSubredditSubmission(subredditName);
        if (randomSubmission.isNsfw()) {
            // We lazily allow the user to add new subreddits to the app without checking
            //   if they are safe for work. That means one of these randomly selected ones
            //   could be one of those. Let's remove it from our list if we detect that.

            return false;
        }

        Log.d(TAG, String.format(">>> subreddit = %s <<<<<<", randomSubmission.getSubredditName()));
        Log.d(TAG, String.format("title = %s", randomSubmission.getTitle()));
        Log.d(TAG, String.format("author = %s", randomSubmission.getAuthor()));
        Log.d(TAG, String.format("created_utc = %s", randomSubmission.getCreatedUtc().toString()));
        Log.d(TAG, String.format("self_text = %s", randomSubmission.getSelftext()));
        Log.d(TAG, String.format("short_url = %s", randomSubmission.getShortURL()));
        Log.d(TAG, String.format("url = %s", randomSubmission.getUrl()));
        Log.d(TAG, String.format("thumbnail = %s", randomSubmission.getThumbnail()));
        Log.d(TAG, String.format("thumbnail_type = %s", randomSubmission.getThumbnailType().toString()));
        Log.d(TAG, String.format("post_hint = %s", randomSubmission.getPostHint()));
        Log.d(TAG, String.format("perma_link = %s", randomSubmission.getPermalink()));
        Log.d(TAG, String.format("preview = %s", randomSubmission.data("preview")));
        return true;
    }
}
