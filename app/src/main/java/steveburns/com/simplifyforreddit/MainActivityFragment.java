package steveburns.com.simplifyforreddit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import steveburns.com.simplifyforreddit.data.SubredditsContract;
import steveburns.com.simplifyforreddit.data.UpdaterService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final int GET_MORE_SUBMISSIONS_IF_BELOW = 3;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = null;

        int remainingSubmissions = 0;
        Cursor cursor = getContext().getContentResolver().query(SubredditsContract.Submissions.buildDirUri(),
                new String[]{"*"}, null, null, SubredditsContract.Submissions.DEFAULT_SORT);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                view = inflater.inflate(R.layout.fragment_main, container, false);

                // Save this submission's id for later use.
                int submissionId = cursor.getInt(cursor.getColumnIndex(SubredditsContract.Submissions._ID));

                // bind values to the view
                bindSubmissionToUi(cursor, view);

                do {
                    // let's determine how many remaining submissions we have locally.
                    remainingSubmissions++;
                } while(cursor.moveToNext());

                // remove this one from the local storage
                getContext().getContentResolver().delete(SubredditsContract.Submissions.buildItemUri(submissionId), null, null);
            }
            cursor.close();
        }

        if (view == null) {
            // There's no submissions in the local database to show.
            // This is either the first time the app's been run or we out of submissions and need to get more.

            // Load empty view
            // TODO: build an empty view and load it here
            view = inflater.inflate(R.layout.fragment_main, container, false);
        }


        // request more submissions from the server if we don't have many remaining
        if (remainingSubmissions < GET_MORE_SUBMISSIONS_IF_BELOW) {
            requestMoreSubmissions();
        }

        return view;
    }

    /**
     * Bind values at current cursor location to UI
     */
    private void bindSubmissionToUi(Cursor cursor, View view) {

        // The URL is the link to the Reddit article or some other internet site...
        // TODO: This needs to be used to make the intent to display the web content
        String url = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.URL));


        // Image
        String thumbnail = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.THUMBNAIL));
        String thumbnailType = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.THUMBNAIL_TYPE));
        if (!TextUtils.isEmpty(thumbnail) && !TextUtils.isEmpty(thumbnailType) && "url".equals(thumbnailType.toLowerCase())) {
            ImageView imageView = (ImageView) view.findViewById(R.id.fragment_main_image);
            imageView.setVisibility(View.VISIBLE);
            Picasso.with(this.getContext())
                    .load(thumbnail)
                    .into(imageView);
        } else {
            view.findViewById(R.id.fragment_main_image).setVisibility(View.GONE);
        }

        // Title
        TextView titleView = (TextView) view.findViewById(R.id.fragment_main_title);
        titleView.setText(cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.TITLE)));

        // Text
        String selfText = cursor.getString(cursor.getColumnIndex(SubredditsContract.Submissions.SELF_TEXT));
        if (!TextUtils.isEmpty(selfText)) {
            view.findViewById(R.id.fragment_main_text_label).setVisibility(View.VISIBLE);
            TextView selfTextView = (TextView) view.findViewById(R.id.fragment_main_text);
            selfTextView.setVisibility(View.VISIBLE);
            selfTextView.setText(selfText);
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
            commentView.setText(comment1);
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
            commentView.setText(comment2);
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
            commentView.setText(comment3);
        } else {
            view.findViewById(R.id.fragment_main_comment_label_3).setVisibility(View.GONE);
            view.findViewById(R.id.fragment_main_comment_3).setVisibility(View.GONE);
        }
    }

    /**
     * Call this method to get more random posts from the server
     */
    private void requestMoreSubmissions() {
        Intent intent = new Intent(getActivity(), UpdaterService.class)
                .setAction(UpdaterService.ACTION_GET_RANDOM_SUBMISSIONS);
        getActivity().startService(intent);
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
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

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_GET_RANDOM_SUBMISSIONS.equals(intent.getAction())) {

                Log.d(TAG, "Received broadcast: BROADCAST_ACTION_GET_RANDOM_POSTS");
                updateRefreshingUI();
            }
        }
    };

    private void updateRefreshingUI() { }

}
