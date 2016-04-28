package steveburns.com.simplifyforreddit.data;

import android.net.Uri;

/**
 * Created by sburns on 4/23/16.
 */
public class SubredditsContract {
    public static final String CONTENT_AUTHORITY = "steveburns.com.simplifyforreddit";
    public static final Uri BASE_URI = Uri.parse("content://steveburns.com.simplifyforreddit");

    interface SubredditsColumns {
        /** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
        String _ID = "_id";
        /** Type: TEXT */
        String NAME = "name";
        /** Type: TEXT */
        String DESCRIPTION = "description";
    }

    public static class Subreddits implements SubredditsColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.steveburns.com.simplifyforreddit.items";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.steveburns.com.simplifyforreddit.items";

        public static final String DEFAULT_SORT = NAME + " DESC";

        /** Matches: /items/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("items").build();
        }

        /** Matches: /items/[_id]/ */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath("items").appendPath(Long.toString(_id)).build();
        }

        /** Read item ID item detail URI. */
        public static long getItemId(Uri itemUri) {
            return Long.parseLong(itemUri.getPathSegments().get(1));
        }
    }
}
