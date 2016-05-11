package steveburns.com.simplifyforreddit.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;

import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sburns on 4/21/16.
 */
public class UpdaterService extends IntentService {
    private static final String TAG = UpdaterService.class.getSimpleName();

    // Get random submission action
    public static final String ACTION_GET_RANDOM_SUBMISSIONS = "steveburns.com.simplifyforreddit.intent.ACTION_GET_RANDOM_SUBMISSIONS";
    public static final String EXTRA_NUM_RANDOM_SUBMISSIONS = "num_random_submissions";
    public static final Integer EXTRA_NUM_RANDOM_SUBMISSIONS_DEFAULT_VALUE = 5;
    public static final String BROADCAST_ACTION_GET_RANDOM_SUBMISSIONS = "steveburns.com.simplifyforreddit.intent.BROADCAST_GET_RANDOM_SUBMISSIONS";

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
            case ACTION_GET_RANDOM_SUBMISSIONS:
                // setup result intent and broadcast
                int numSubmissionsToGet = intent.getIntExtra(EXTRA_NUM_RANDOM_SUBMISSIONS, EXTRA_NUM_RANDOM_SUBMISSIONS_DEFAULT_VALUE);
                int numStored = getRandomSubmissions(numSubmissionsToGet);
                Intent submissionsIntent = new Intent(BROADCAST_ACTION_GET_RANDOM_SUBMISSIONS);
                submissionsIntent.putExtra(EXTRA_NUM_RANDOM_SUBMISSIONS, numStored);
                sendBroadcast(submissionsIntent);
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

    private synchronized int getRandomSubmissions(int numToGet) {
        int numStored = 0;
        Cursor cursor = null;
        try {
            cursor = getApplicationContext()
                    .getContentResolver()
                    .query(SubredditsContract.Subreddits.buildDirUri(),
                            new String[]{SubredditsContract.Subreddits._ID, SubredditsContract.Subreddits.NAME},
                            null,
                            null,
                            SubredditsContract.Subreddits.DEFAULT_SORT);
        } catch(Exception e) {
            Log.e(TAG, e.toString());
            return numStored;
        }

        if (cursor != null) {
            List<Pair<Long, String>> subreddits = new ArrayList<>();
            try {
                if(cursor.moveToFirst()) {
                    do {
                        Long id = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Subreddits._ID));
                        String name = cursor.getString(cursor.getColumnIndex(SubredditsContract.Subreddits.NAME));
                        if (!TextUtils.isEmpty(name)) {
                            subreddits.add(new Pair<>(id, name));
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
                    Log.d(TAG, String.format("Ramdomly selected subreddit: %s", subreddits.get(pos).second));
                    if(storeRandomSubmissionForSubreddit(subreddits.get(pos).first, subreddits.get(pos).second)) {
                        numStored++;
                    }
                }
            }
        }
        return numStored;
    }

    /**
     * Queries for a random submission in the given subreddit.
     * @param subredditName - name of the subreddit
     * @return result
     */
    private boolean storeRandomSubmissionForSubreddit(Long id, String subredditName) {

        Submission randomSubmission = RedditData.getSubredditSubmission(subredditName);
        if (randomSubmission == null) {
            return false;
        }

        if (randomSubmission.isNsfw()) {
            // We lazily allow the user to add new subreddits to the app without checking
            //   if they are safe for work. That means one of these randomly selected ones
            //   might not be safe. Let's remove it from our list if we detect that.

            getApplicationContext().getContentResolver().delete(
                    SubredditsContract.Subreddits.buildItemUri(id), null, null);
            return false;

        }

        ContentValues cv = new ContentValues();
        SimpleDateFormat formatter = new SimpleDateFormat("d MMM yyyy");

        cv.put(SubredditsContract.Submissions.VIEWED, 0);
        cv.put(SubredditsContract.Submissions.SUBREDDIT_NAME, randomSubmission.getSubredditName());
        cv.put(SubredditsContract.Submissions.TITLE, randomSubmission.getTitle().trim());
        cv.put(SubredditsContract.Submissions.AUTHOR, randomSubmission.getAuthor());
        cv.put(SubredditsContract.Submissions.CREATE_DATE_UTC, formatter.format(randomSubmission.getCreatedUtc()));
        cv.put(SubredditsContract.Submissions.SELF_TEXT, randomSubmission.getSelftext().trim());
        cv.put(SubredditsContract.Submissions.URL, randomSubmission.getUrl());
        cv.put(SubredditsContract.Submissions.THUMBNAIL, randomSubmission.getThumbnail());
        cv.put(SubredditsContract.Submissions.THUMBNAIL_TYPE, randomSubmission.getThumbnailType().toString());
        cv.put(SubredditsContract.Submissions.POST_HINT, randomSubmission.getPostHint().toString());
        cv.put(SubredditsContract.Submissions.PERMA_LINK, randomSubmission.getPermalink());
        cv.put(SubredditsContract.Submissions.COMMENT_COUNT, randomSubmission.getCommentCount().toString());

        // Submission comments
        final int maxNumComments = 3;  // Limit the number of comments we store to this
        final int maxCommentLen = 256; // Limit the comment length to this
        int i = 0;
        CommentNode commentNode = randomSubmission.getComments();
        for (CommentNode comment : commentNode) {
            if (i >= maxNumComments) {
                break;
            }
            // Let's store the first three of the top level comments
            String commentText = comment.getComment().getBody();
            if (!TextUtils.isEmpty(commentText)) {
                commentText = commentText.length() <= maxCommentLen ? commentText : commentText.substring(0, maxCommentLen);
                String commentField = i==0 ? SubredditsContract.Submissions.COMMENT_1 : i==1 ? SubredditsContract.Submissions.COMMENT_2 : SubredditsContract.Submissions.COMMENT_3;
                cv.put(commentField, commentText);
                i++;
            }
        }

        // Save it!
        getApplicationContext().getContentResolver().insert(
                SubredditsContract.Submissions.buildDirUri(), cv);

        return true;
    }
}
