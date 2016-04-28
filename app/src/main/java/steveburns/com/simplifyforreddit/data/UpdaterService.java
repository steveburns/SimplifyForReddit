package steveburns.com.simplifyforreddit.data;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import net.dean.jraw.models.Subreddit;

/**
 * Created by sburns on 4/21/16.
 */
public class UpdaterService extends IntentService {
    private static final String TAG = UpdaterService.class.getSimpleName();

    // Get random post action
    public static final String ACTION_GET_RANDOM_POSTS = "steveburns.com.simplifyforreddit.intent.ACTION_GET_RANDOM_POSTS";
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
}
