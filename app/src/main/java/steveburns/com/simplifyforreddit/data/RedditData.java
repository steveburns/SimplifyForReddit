package steveburns.com.simplifyforreddit.data;

import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Subreddit;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import steveburns.com.simplifyforreddit.AuthConstants;

/**
 * Created by sburns on 4/20/16.
 */
public class RedditData {
    private static final String TAG = RedditData.class.getSimpleName();
    private static final String APP_ID = "simplifyforreddit";
    private static final String PLATFORM = "android";
    private static final String VERSION = "v0.1";

    /* To compile and run, you must provide the following values by adding an AuthConstants class like this.
    -- Note that this file is referenced in .gitignore and therefore will not be placed into version control
    public class AuthConstants {
        public static final String CLIENT_ID = "PUT_CLIENTID_HERE";
        public static final String APP_CREATOR = "PUT_USERNAME_HERE";
        public static final String CLIENT_SECRET = "PUT_CLIENT_SECRET_HERE";
        public static final String PASSWORD = "PUT_PASSWORD_HERE";
    }*/
    private static final String CLIENT_ID = AuthConstants.CLIENT_ID;
    private static final String APP_CREATOR = AuthConstants.APP_CREATOR;
    private static final String CLIENT_SECRET = AuthConstants.CLIENT_SECRET;
    private static final String PASSWORD = AuthConstants.PASSWORD;


    private static RedditClient mRedditClient = null;

    private static RedditClient getAuthorizedClient(boolean forceRenewal) {

        // If we're not forcing a renewal and we already have a client assume it's gonna work.
        if (!forceRenewal && mRedditClient != null) {
            return mRedditClient;
        }

        UserAgent userAgent = UserAgent.of(PLATFORM, APP_ID, VERSION, APP_CREATOR);

        mRedditClient = new RedditClient(userAgent);
        Credentials credentials = Credentials.script(APP_CREATOR, PASSWORD, CLIENT_ID, CLIENT_SECRET);

        try {
            OAuthData authData = mRedditClient.getOAuthHelper().easyAuth(credentials);
            mRedditClient.authenticate(authData);
        } catch (RuntimeException | OAuthException e) {
            Log.d(TAG, e.toString());
            mRedditClient = null;
        }
        return mRedditClient;
    }

    public static Subreddit getSubreddit(String subredditName) {

        int maxAttempts = 2;
        Subreddit subreddit = null;
        for(int attempt = 1; attempt <= maxAttempts && subreddit == null; attempt++) {
            try {
                // force the auth renewal if we're past the first attempt
                subreddit = getAuthorizedClient(attempt > 1).getSubreddit(subredditName);
            } catch (RuntimeException e) {
                Log.d(TAG, e.toString());
            }
        }
        return subreddit;
    }

    public static Submission getSubredditSubmission(String subredditName) {
        int maxAttempts = 2;
        Submission subredditSubmission = null;
        for(int attempt = 1; attempt <= maxAttempts && subredditSubmission == null; attempt++) {
            try {
                // force the auth renewal if we're past the first attempt
                subredditSubmission = getAuthorizedClient(attempt > 1).getRandomSubmission(subredditName);
            } catch (RuntimeException e) {
                Log.d(TAG, e.toString());
            }
        }
        return subredditSubmission;
    }
}
