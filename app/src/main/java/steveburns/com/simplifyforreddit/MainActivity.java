package steveburns.com.simplifyforreddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane = false;
    private ViewPager mViewPager;
    private SubmissionsPagerAdapter mSubmissionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        This might be the way to implement the swipe action:
        - Also add the main fragment using a transaction like we already do with the subreddit fragment for two pane mode.
        - When the user swipes, use a new instance of the main fragment to replace the old fragment.
         */

        // Okay, create the main fragment
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_main_container, new MainActivityFragment(), "")
//                .commit();

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mSubmissionsPagerAdapter = new SubmissionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSubmissionsPagerAdapter);

        // Are we in tablet mode?
        if(findViewById(R.id.fragment_subreddit_list_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                // tablet mode also shows the list of subreddits.
/*
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_subreddit_list_container, SubredditsListFragment.newInstance(1), "")
                        .commit();
*/

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_subreddit_list_container, new SubredditsListFragmentSimple(), "")
                        .commit();
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        try {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
/*
                Log.d(TAG, "In fab.setOnClickListener");
                Snackbar.make(view, "Replace with your own action!!!!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
*/

//                    refresh();
                }
            });
        } catch(NullPointerException e) {

        }

/* populate the initial list of subreddits
        if (savedInstanceState == null) {
            refresh();
        }
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mTwoPane) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, SubredditsListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
