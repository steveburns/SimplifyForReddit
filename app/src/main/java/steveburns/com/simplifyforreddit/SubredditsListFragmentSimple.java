package steveburns.com.simplifyforreddit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import steveburns.com.simplifyforreddit.data.SubredditsContract;

/**
 * Created by sburns on 5/11/16.
 */
public class SubredditsListFragmentSimple extends Fragment {

    private static final String KEY_SUBREDDITS_LIST = "subreddits";

    private ArrayList<SubredditItem> mSubredditsList = null;
    private SubredditsAdapter mSubredditsAdapter = null;
    private ListView mListView = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddits_list_simple, container, false);

        if (savedInstanceState != null) {
            // being restored after a rotation?
            restoreInstanceState(savedInstanceState);
            Log.d("onCreateView", "savedInstanceState is NOT null");
        }

        if (mSubredditsList == null) {
            populateSubredditsLists();
        }
        if (mSubredditsAdapter == null) {
            mSubredditsAdapter = new SubredditsAdapter(getActivity(), mSubredditsList);
        }

        mListView = (ListView) view.findViewById(R.id.listView);
        mListView.setAdapter(mSubredditsAdapter);

        return view;
    }

    private void populateSubredditsLists() {
        mSubredditsList = new ArrayList<>();
        Cursor cursor = getContext().getContentResolver().query(SubredditsContract.Subreddits.buildDirUri(),
                new String[]{"*"}, null, null, SubredditsContract.Submissions.DEFAULT_SORT);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Long id = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Subreddits._ID));
                    String name = cursor.getString(cursor.getColumnIndex(SubredditsContract.Subreddits.NAME));
                    mSubredditsList.add(new SubredditItem(id, name));
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_SUBREDDITS_LIST, mSubredditsList);
        super.onSaveInstanceState(outState);
    }

    public void restoreInstanceState(Bundle savedState) {
        mSubredditsList = savedState.getParcelableArrayList(KEY_SUBREDDITS_LIST);
    }
}
