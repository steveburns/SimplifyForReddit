package steveburns.com.simplifyforreddit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import steveburns.com.simplifyforreddit.data.UpdaterService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                    refresh();
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

    private void refresh() {
        Intent intent = new Intent(this, UpdaterService.class)
            .setAction(UpdaterService.ACTION_GET_RANDOM_POSTS);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_GET_RANDOM_POSTS));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        if (UpdaterService.BROADCAST_ACTION_GET_RANDOM_POSTS.equals(intent.getAction())) {
            updateRefreshingUI();
        }
        }
    };

    private void updateRefreshingUI() { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
