package steveburns.com.simplifyforreddit.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Created by sburns.
 */
public class SubredditLoader extends CursorLoader {
    public static SubredditLoader newAllSubredditInstance(Context context) {
        return new SubredditLoader(context, SubredditsContract.Subreddits.buildDirUri());
    }

    public static SubredditLoader newInstanceForItemId(Context context, long itemId) {
        return new SubredditLoader(context, SubredditsContract.Subreddits.buildItemUri(itemId));
    }

    private SubredditLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, SubredditsContract.Subreddits.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                SubredditsContract.Subreddits._ID,
                SubredditsContract.Subreddits.NAME
        };

        int _ID = 0;
        int NAME = 1;
    }
}