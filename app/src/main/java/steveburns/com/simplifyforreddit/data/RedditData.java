package steveburns.com.simplifyforreddit.data;

import android.util.Log;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Subreddit;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sburns on 4/20/16.
 */
public class RedditData {
    private static final String TAG = RedditData.class.getSimpleName();
    private static final String APP_ID = "simplifyforreddit";
    private static final String CLIENT_ID = "-fDYP2zYaDM6cw";
    private static final String PLATFORM = "android";
    private static final String VERSION = "v0.1";
    private static final String APP_CREATOR = "PUT_USERNAME_HERE";
    private static final String CLIENT_SECRET = "PUT_CLIENT_SECRET_HERE";
    private static final String PASSWORD = "PUT_PASSWORD_HERE";


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

//    // TODO:
//    // Should we hold onto the Auth stuff between calls to get data and only
//    //   renew if we need to or do we Auth everytime?
//    // OR do we hold on to the RedditClient????
//
//    public static void testIt() {
//        UserAgent userAgent = UserAgent.of(PLATFORM, APP_ID, VERSION, APP_CREATOR);
//
//        RedditClient redditClient = new RedditClient(userAgent);
////        Credentials credentials = Credentials.userlessApp(APP_ID, DEVICE_ID);
////        Credentials credentials = Credentials.userless(APP_ID, CLIENT_SECRET, DEVICE_ID);
//        Credentials credentials = Credentials.script(APP_CREATOR, "asdfasdf!*", "-fDYP2zYaDM6cw", "4I_FHAABGE-w_ocCXeHbeAz3p6c");
//
//        /*
//You must supply your OAuth2 client's credentials via HTTP Basic Auth for this request.
//The "user" is the client_id,
//the "password" is the client_secret
//
//reddit account:
//burnssteve / asdfasdf!*
//        */
//
//        try {
////            OAuthData authData = redditClient.getOAuthHelper().easyAuth(credentials);
//            OAuthData authData = redditClient.getOAuthHelper().easyAuth(credentials);
//
//            redditClient.authenticate(authData);
//
//            HashMap<String, String> nameToDescription = new HashMap<>();
//            ArrayList<String> subreddits = new ArrayList<>(10);
//            subreddits.add("android");
//            subreddits.add("art");
//            subreddits.add("askscience");
//            subreddits.add("books");
//            subreddits.add("diy");
//            subreddits.add("documentaries");
//            subreddits.add("fitness");
//            subreddits.add("food");
//            subreddits.add("futurology");
//            subreddits.add("gadgets");
//            subreddits.add("gaming");
//            subreddits.add("getmotivated");
//            subreddits.add("history");
//            subreddits.add("ios");
//            subreddits.add("lifeprotips");
//            subreddits.add("mildlyinteresting");
//            subreddits.add("mobile");
//            subreddits.add("movies");
//            subreddits.add("music");
//            subreddits.add("news");
//            subreddits.add("personalfinance");
//            subreddits.add("philosophy");
//            subreddits.add("photoshopbattles");
//            subreddits.add("science");
//            subreddits.add("space");
//            subreddits.add("sports");
//            subreddits.add("worldnews");
//
//            for(String name : subreddits) {
//                Subreddit sr = redditClient.getSubreddit(name);
//
////                String displayName = sr.getDisplayName();
////                String HeaderImage = sr.getHeaderImage();
////                String headerTitle = sr.getHeaderTitle();
////                String title = sr.getTitle();
//                Boolean isNsfw = sr.isNsfw();
//                String publicDescription = sr.getPublicDescription();
////                Subreddit.Type type = sr.getSubredditType();
//
//                String nothing = "nothing";
//
//                nameToDescription.put(name, publicDescription);
//            }
//
//            String data = nameToDescription.toString();
//            Log.d(TAG, data);
//        } catch (RuntimeException e) {
//            Log.d(TAG, e.toString());
//        } catch (OAuthException e) {
//            Log.d(TAG, e.toString());
//        }
//
//    }
}
