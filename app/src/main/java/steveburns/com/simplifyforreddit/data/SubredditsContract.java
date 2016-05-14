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
    }

    public static class Subreddits implements SubredditsColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.steveburns.com.simplifyforreddit.items";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.steveburns.com.simplifyforreddit.items";

        public static final String DEFAULT_SORT = NAME + " ASC";

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

    interface SubmissionColumns {
        String _ID = "_id";
        String VIEWED = "viewed";
        String SUBREDDIT_NAME = "subreddit_name";
        String TITLE = "title";
        String AUTHOR = "author";
        String CREATE_DATE_UTC = "create_date_utc";
        String SELF_TEXT = "self_text";
        String URL = "url";
        String THUMBNAIL = "thumbnail";
        String THUMBNAIL_TYPE = "thumbnail_type";
        String POST_HINT = "post_hint";
        String PERMA_LINK = "perma_link";
        String COMMENT_COUNT = "comment_count";
        String COMMENT_1 = "comment_1";
        String COMMENT_2 = "comment_2";
        String COMMENT_3 = "comment_3";
    }

    public static class Submissions implements SubmissionColumns {
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.steveburns.com.simplifyforreddit.submissions";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.steveburns.com.simplifyforreddit.submissions";

        public static final String DEFAULT_SORT = _ID + " ASC";

        /** Matches: /submissions/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("submissions").build();
        }

        /** Matches: /submissions/[_id]/ */
        public static Uri buildItemUri(long _id) {
            return BASE_URI.buildUpon().appendPath("submissions").appendPath(Long.toString(_id)).build();
        }
    }
}
