package steveburns.com.simplifyforreddit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

    private ArrayList<SubredditItem> mSubredditsList = null;
    private SubredditsAdapter mSubredditsAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddits_list_simple, container, false);

        populateSubredditsList();
        if (mSubredditsAdapter == null) {
            mSubredditsAdapter = new SubredditsAdapter(getActivity(), mSubredditsList);
        }

        ((ListView) view.findViewById(R.id.listView)).setAdapter(mSubredditsAdapter);

        setupAddSubredditButton(view);
        return view;
    }

    private void setupAddSubredditButton(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = SubredditsListFragmentSimple.this.getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag(Constants.ADD_SUBREDDIT_FRAG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                AddSubredditFragment fragment = AddSubredditFragment.newInstance(new Action() {
                    @Override
                    public void execute() {
                        populateSubredditsList();
                        mSubredditsAdapter.notifyDataSetChanged();
                    }
                });
                fragment.show(ft, Constants.ADD_SUBREDDIT_FRAG_TAG);
            }
        });
    }

    private void populateSubredditsList() {
        if (mSubredditsList == null) {
            mSubredditsList = new ArrayList<>();
        } else {
            mSubredditsList.clear();
        }
        Cursor cursor = getContext().getContentResolver().query(SubredditsContract.Subreddits.buildDirUri(),
                new String[]{"*"}, null, null, SubredditsContract.Subreddits.DEFAULT_SORT);
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
}
