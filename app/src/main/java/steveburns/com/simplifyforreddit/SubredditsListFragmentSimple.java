package steveburns.com.simplifyforreddit;

import android.app.LoaderManager;
import android.content.Loader;
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

import steveburns.com.simplifyforreddit.data.SubredditLoader;

/**
 * Created by sburns on 5/11/16.
 */
public class SubredditsListFragmentSimple extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int LOADER_IDENTIFIER = 100;
    private SubredditsAdapter mSubredditsAdapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddits_list_simple, container, false);

        mSubredditsAdapter = new SubredditsAdapter(getActivity(), null, 0);
        getActivity().getLoaderManager().initLoader(LOADER_IDENTIFIER, null, this);

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
                        mSubredditsAdapter.notifyDataSetChanged();
                    }
                });
                fragment.show(ft, Constants.ADD_SUBREDDIT_FRAG_TAG);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return SubredditLoader.newAllSubredditInstance(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mSubredditsAdapter.swapCursor(cursor);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSubredditsAdapter.swapCursor(null);
    }
}
