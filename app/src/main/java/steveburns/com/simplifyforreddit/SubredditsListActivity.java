package steveburns.com.simplifyforreddit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class SubredditsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddits_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag(Constants.ADD_SUBREDDIT_FRAG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                AddSubredditFragment fragment = AddSubredditFragment.newInstance();
                fragment.show(ft, Constants.ADD_SUBREDDIT_FRAG_TAG);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
