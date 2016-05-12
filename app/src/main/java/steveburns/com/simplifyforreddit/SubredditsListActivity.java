package steveburns.com.simplifyforreddit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SubredditsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_subreddits_list);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_subreddit_list_container, new SubredditsListFragmentSimple(), "")
                .commit();

    }
}
