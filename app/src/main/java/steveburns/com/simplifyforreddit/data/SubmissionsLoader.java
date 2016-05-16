package steveburns.com.simplifyforreddit.data;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

/**
 * Created by sburns.
 */
public class SubmissionsLoader extends CursorLoader {
    public static SubmissionsLoader newAllSubmissionsInstance(Context context) {
        return new SubmissionsLoader(context, SubredditsContract.Submissions.buildDirUri());
    }

    public static SubmissionsLoader newInstanceForItemId(Context context, long itemId) {
        return new SubmissionsLoader(context, SubredditsContract.Submissions.buildItemUri(itemId));
    }

    private SubmissionsLoader(Context context, Uri uri) {
        super(context, uri, new String[]{"*"}, null, null, SubredditsContract.Submissions.DEFAULT_SORT);
    }
}