package steveburns.com.simplifyforreddit;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import steveburns.com.simplifyforreddit.data.SubredditsContract;
import steveburns.com.simplifyforreddit.data.UpdaterService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final String BUNDLE_SUBMISSION_ID = "Bundle_Submission_id";
    private static final int GET_MORE_SUBMISSIONS_IF_BELOW = 5;

    private long mSubmissionId = 0; // ID of the most recently loaded submission

    public MainActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_SUBMISSION_ID, mSubmissionId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedState) {

        View view = null;
        if (savedState != null && (mSubmissionId = savedState.getLong(BUNDLE_SUBMISSION_ID)) > 0) {
            Log.d(TAG, "savedState != null. Reload CURRENT submission");
            view = ReloadCurrentSubmission(inflater, container);
            if (view == null) {
                TextView textView = new TextView(getContext());
                textView.setText("Cannot reload submission. The data has been removed from the app.");
                view = textView;
            }
        } else {
            // Load the next locally stored submission.
            Log.d(TAG, "savedState == null. Loading NEXT submission");
            view = LoadNextSubmission(inflater, container);

            // Don't have a view so we must not have any locally stored submissions... yet!
            if (view == null) {
                view = inflater.inflate(R.layout.loading_data, container, false);
            }
        }

        if (view != null) {
            AdView mAdView = (AdView) view.findViewById(R.id.adView);
            if (mAdView != null) {
                // Create an ad request. Check logcat output for the hashed device ID to
                // get test ads on a physical device. e.g.
                // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();
                mAdView.loadAd(adRequest);
            }
        }

        return view;
    }

    /**
     * Reload current submission
     */
    private View ReloadCurrentSubmission(LayoutInflater inflater, ViewGroup container) {

        View view = null;
        Cursor cursor = getContext().getContentResolver().query(SubredditsContract.Submissions.buildItemUri(mSubmissionId),
                new String[]{"*"}, null, null,
                SubredditsContract.Submissions.DEFAULT_SORT);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                view = inflater.inflate(R.layout.fragment_main, container, false);
                bindSubmissionToUi(cursor, view);
            }
            cursor.close();
        }

        return view;
    }

    /**
     * Load the next submission.
     */
    private View LoadNextSubmission(LayoutInflater inflater, ViewGroup container) {

        View view = null;
        int numRemainingSubmissions = 0;
        long foundSubmissionId = 0;

        Cursor cursor = getContext().getContentResolver().query(SubredditsContract.Submissions.buildDirUri(),
                new String[]{"*"}, null, null, SubredditsContract.Submissions.DEFAULT_SORT);
        try {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        Long viewed = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Submissions.VIEWED));
                        if (foundSubmissionId > 0) {
                            numRemainingSubmissions++;
                        } else if (viewed == 0) {
                            foundSubmissionId = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Submissions._ID));
                            view = inflater.inflate(R.layout.fragment_main, container, false);
                            bindSubmissionToUi(cursor, view);
                            mSubmissionId = foundSubmissionId;
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
            getContext().getContentResolver().update(SubredditsContract.Submissions.buildItemUri(foundSubmissionId), cv, null, null);
        }

        // request more submissions from the server if we don't have many remaining
        Log.d(TAG, String.format("numRemainingSubmissions = %d", numRemainingSubmissions));
        if (numRemainingSubmissions < GET_MORE_SUBMISSIONS_IF_BELOW) {
            requestMoreSubmissions();
        }

        return view;
    }

    /**
     * Bind values at current cursor location to UI
     */
    private void bindSubmissionToUi(Cursor cursor, View view) {

        View.OnClickListener clickListener = null;
        final String url = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.URL));
        if (!TextUtils.isEmpty(url)) {
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            };
        }

        // Image
        String thumbnail = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.THUMBNAIL));
        String thumbnailType = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.THUMBNAIL_TYPE));
        if (!TextUtils.isEmpty(thumbnail) && !TextUtils.isEmpty(thumbnailType) && "url".equals(thumbnailType.toLowerCase())) {
            ImageView imageView = (ImageView) view.findViewById(R.id.fragment_main_image);
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(this.getContext())
                    .load(thumbnail)
                    .into(imageView);
            if (clickListener != null) {
                imageView.setOnClickListener(clickListener);
            }
        } else {
            view.findViewById(R.id.fragment_main_image).setVisibility(View.GONE);
        }

        // Title
        String titleText = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.TITLE));
        TextView titleView = (TextView) view.findViewById(R.id.fragment_main_title);
        TextView moreView = (TextView) view.findViewById(R.id.fragment_main_more);
        if (clickListener != null) {
            titleView.setOnClickListener(clickListener);
            moreView.setOnClickListener(clickListener);
            moreView.setVisibility(View.VISIBLE);
        } else {
            moreView.setVisibility(View.GONE);
        }
        titleView.setText(Html.fromHtml(titleText));

        // Text
        String selfText = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.SELF_TEXT));
        if (!TextUtils.isEmpty(selfText)) {
            view.findViewById(R.id.fragment_main_text_label).setVisibility(View.VISIBLE);
            TextView selfTextView = (TextView) view.findViewById(R.id.fragment_main_text);
            selfTextView.setVisibility(View.VISIBLE);
            selfTextView.setText(Html.fromHtml(selfText));
        } else {
            view.findViewById(R.id.fragment_main_text).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_main_text_label).setVisibility(View.GONE);
        }

        TextView authorView = (TextView) view.findViewById(R.id.fragment_main_author);
        authorView.setText(cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.AUTHOR)));

        TextView createDateView = (TextView) view.findViewById(R.id.fragment_main_created);
        createDateView.setText(cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.CREATE_DATE_UTC)));

        TextView nameView = (TextView) view.findViewById(R.id.fragment_main_subreddit_name);
        nameView.setText(cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.SUBREDDIT_NAME)));

        TextView totalComments = (TextView) view.findViewById(R.id.fragment_main_total_comments);
        totalComments.setText(
                String.format(getContext().getString(R.string.total_comments_label),
                        cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.COMMENT_COUNT)))
        );

        // Comment 1
        String comment1 = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.COMMENT_1));
        if (!TextUtils.isEmpty(comment1)) {
            view.findViewById(R.id.fragment_main_comment_label_1).setVisibility(View.VISIBLE);
            TextView commentView = (TextView) view.findViewById(R.id.fragment_main_comment_1);
            commentView.setVisibility(View.VISIBLE);
            commentView.setText(Html.fromHtml(comment1));
        } else {
            view.findViewById(R.id.fragment_main_comment_label_1).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_main_comment_1).setVisibility(View.GONE);
        }

        // Comment 2
        String comment2 = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.COMMENT_2));
        if (!TextUtils.isEmpty(comment2)) {
            view.findViewById(R.id.fragment_main_comment_label_2).setVisibility(View.VISIBLE);
            TextView commentView = (TextView) view.findViewById(R.id.fragment_main_comment_2);
            commentView.setVisibility(View.VISIBLE);
            commentView.setText(Html.fromHtml(comment2));
        } else {
            view.findViewById(R.id.fragment_main_comment_label_2).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_main_comment_2).setVisibility(View.GONE);
        }

        // Comment 3
        String comment3 = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.COMMENT_3));
        if (!TextUtils.isEmpty(comment3)) {
            view.findViewById(R.id.fragment_main_comment_label_3).setVisibility(View.VISIBLE);
            TextView commentView = (TextView) view.findViewById(R.id.fragment_main_comment_3);
            commentView.setVisibility(View.VISIBLE);
            commentView.setText(Html.fromHtml(comment3));
        } else {
            view.findViewById(R.id.fragment_main_comment_label_3).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_main_comment_3).setVisibility(View.GONE);
        }
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_GET_RANDOM_SUBMISSIONS));
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().unregisterReceiver(mRefreshingReceiver);
    }

    /**
     * Call this method to get more random posts from the server
     */
    private void requestMoreSubmissions() {
        Intent intent = new Intent(getActivity(), UpdaterService.class)
                .setAction(UpdaterService.ACTION_GET_RANDOM_SUBMISSIONS);
        getActivity().startService(intent);
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_GET_RANDOM_SUBMISSIONS.equals(intent.getAction())) {
                updateRefreshingUI();
//                Log.d(TAG, String.format("Received broadcast: BROADCAST_ACTION_GET_RANDOM_POSTS, Local submissions: %d", countLocalSubmissions()));
            }
        }
    };

    private int countLocalSubmissions() {
        int num = 0;

        Cursor cursor = getContext().getContentResolver().query(SubredditsContract.Submissions.buildDirUri(),
                new String[]{"*"}, null, null, SubredditsContract.Submissions.DEFAULT_SORT);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    num++;
                } while(cursor.moveToNext());
            }
            cursor.close();
        }

        return num;
    }

    static final Object sync = new Object();
    private void updateRefreshingUI() {
        synchronized(sync) {
            View view = this.getView();
            if (view != null) {
                if (view.findViewById(R.id.fragment_loading_message) != null) {

                    // Reload the fragment and it will automatically load the data.
                    getFragmentManager().beginTransaction()
                            .detach(this)
                            .attach(this)
                            .commit();
                }
            }
        }
    }

}
