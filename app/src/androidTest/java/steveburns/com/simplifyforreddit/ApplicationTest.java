package steveburns.com.simplifyforreddit;

import android.app.Application;
import android.test.ApplicationTestCase;

import net.dean.jraw.models.Subreddit;

import steveburns.com.simplifyforreddit.data.RedditData;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testSomething() {

        final String requestedSubreddit = "android";

        Subreddit subreddit = RedditData.getSubreddit(requestedSubreddit);
        assertNotNull(subreddit);
        assertTrue(requestedSubreddit.equalsIgnoreCase(subreddit.getDisplayName()));

/*

        final BroadcastReceiver getSubredditReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (UpdaterService.BROADCAST_ACTION_GET_SUBREDDIT.equals(intent.getAction())) {
                    assertEquals("Requested subreddit names do not match",
                            requestedSubreddit,
                            intent.getStringExtra(UpdaterService.EXTRA_SUBREDDIT_NAME));
                }
            }
        };
*/

/*
        getContext().registerReceiver(getSubredditReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_GET_SUBREDDIT));

        getContext().startService(
                new Intent(getContext(), UpdaterService.class)
                        .setAction(UpdaterService.ACTION_GET_SUBREDDIT)
                        .putExtra(UpdaterService.EXTRA_SUBREDDIT_NAME, requestedSubreddit));
*/

    }
}