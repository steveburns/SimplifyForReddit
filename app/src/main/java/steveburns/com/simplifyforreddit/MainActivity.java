package steveburns.com.simplifyforreddit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.appinvite.AppInviteInvitation;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int REQUEST_INVITE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SubmissionsPagerAdapter(getSupportFragmentManager()));

        // Are we in tablet mode?
        if(findViewById(R.id.fragment_subreddit_list_container) != null) {
            if (savedInstanceState == null) {
                // tablet mode also shows the list of subreddits.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_subreddit_list_container, new SubredditsListFragmentSimple(), "")
                        .commit();
            }
        }
    }

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

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SubredditsListActivity.class));
            return true;
        } else if (id == R.id.action_share_app) {
            Intent intent = new AppInviteInvitation.IntentBuilder(
                    getString(R.string.invitation_title))
                    .setMessage(getString(R.string.invitation_message))
                    .build();
            startActivityForResult(intent, REQUEST_INVITE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
