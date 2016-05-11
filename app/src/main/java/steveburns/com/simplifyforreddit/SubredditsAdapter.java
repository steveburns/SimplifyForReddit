package steveburns.com.simplifyforreddit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sburns on 5/11/16.
 */
public class SubredditsAdapter extends ArrayAdapter<SubredditItem> {

    public SubredditsAdapter(Context context, List<SubredditItem> SubredditItems) {
        super(context, 0, SubredditItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        SubredditItem subredditItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {

            // TODO: inflate the view once we've defined it.
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track, parent, false);

            // TODO: for now just use a text view.
            convertView = new TextView(getContext());
        }

        ((TextView)convertView).setText(subredditItem.getName());

        return convertView;
    }
}
