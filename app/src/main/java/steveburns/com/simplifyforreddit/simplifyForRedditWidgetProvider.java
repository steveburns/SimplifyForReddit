package steveburns.com.simplifyforreddit;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import org.w3c.dom.Text;

import steveburns.com.simplifyforreddit.data.SubredditsContract;
import steveburns.com.simplifyforreddit.data.UpdaterService;

/**
 * Created by sburns on 5/14/16.
 */
public class simplifyForRedditWidgetProvider extends AppWidgetProvider {

    public static final String TAG = simplifyForRedditWidgetProvider.class.getSimpleName();
    private static final int GET_MORE_SUBMISSIONS_IF_BELOW = 5;

    /**
     * Called in response to the {@link AppWidgetManager#ACTION_APPWIDGET_UPDATE} and
     * {@link AppWidgetManager#ACTION_APPWIDGET_RESTORED} broadcasts when this AppWidget
     * provider is being asked to provide {@link RemoteViews RemoteViews}
     * for a set of AppWidgets.  Override this method to implement your own AppWidget functionality.
     * <p/>
     * {@more}
     *
     * @param context          The {@link Context Context} in which this receiver is
     *                         running.
     * @param appWidgetManager A {@link AppWidgetManager} object you can call {@link
     *                         AppWidgetManager#updateAppWidget} on.
     * @param appWidgetIds     The appWidgetIds for which an update is needed.  Note that this
     *                         may be all of the AppWidget instances for this provider, or just
     *                         a subset of them.
     * @see AppWidgetManager#ACTION_APPWIDGET_UPDATE
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        int numRemainingSubmissions = 0;
        long foundSubmissionId = 0;

        Cursor cursor = context.getContentResolver().query(SubredditsContract.Submissions.buildDirUri(),
                new String[]{"*"}, null, null, SubredditsContract.Submissions.DEFAULT_SORT);
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Long viewed = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Submissions.VIEWED));
                        if (foundSubmissionId > 0) {
                            numRemainingSubmissions++;
                        } else if (viewed == 0) {
                            // found unviewed submission
                            foundSubmissionId = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Submissions._ID));
                            for (int i = 0; i < appWidgetIds.length; i++) {
                                updateAppWidget(context, appWidgetManager, appWidgetIds[i], cursor);
                            }
                        }
                    } while (cursor.moveToNext());
                }
            }
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (foundSubmissionId > 0) {
            // update storage so we know this submission has already been viewed.
            ContentValues cv = new ContentValues(1);
            cv.put(SubredditsContract.Submissions.VIEWED, 1);
            context.getContentResolver().update(SubredditsContract.Submissions.buildItemUri(foundSubmissionId), cv, null, null);
        }

        // request more submissions from the server if we don't have many remaining
        if (numRemainingSubmissions < GET_MORE_SUBMISSIONS_IF_BELOW) {
            requestMoreSubmissions(context);
        }
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, Cursor cursor) {

        // Construct the RemoteViews object
        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.simplify_for_reddit_app_widget);

        // Image
        String thumbnail = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.THUMBNAIL));
        String thumbnailType = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.THUMBNAIL_TYPE));
        if (!TextUtils.isEmpty(thumbnail) && !TextUtils.isEmpty(thumbnailType) && "url".equals(thumbnailType.toLowerCase())) {
            view.setImageViewUri(R.id.widget_image_view, Uri.parse(thumbnail));
        } else {
            view.setViewVisibility(R.id.widget_image_view, View.GONE);
        }

        // Title
        String titleText = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.TITLE));
        if (!TextUtils.isEmpty(titleText)) {
            view.setTextViewText(R.id.widget_title_text, titleText);
        }
        Log.d(TAG, String.format("Setting widget text: %s", titleText));

        // Create an Intent that displays Reddit submission web content
        final String url = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.URL));
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            view.setOnClickPendingIntent(R.id.widget_title_text, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetId, view);
    }

    /**
     * Call this method to get more random posts from the server
     */
    private void requestMoreSubmissions(Context context) {
        Intent intent = new Intent(context, UpdaterService.class)
                .setAction(UpdaterService.ACTION_GET_RANDOM_SUBMISSIONS);
        context.startService(intent);
    }
}
