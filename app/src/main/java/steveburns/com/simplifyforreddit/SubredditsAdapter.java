package steveburns.com.simplifyforreddit;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import steveburns.com.simplifyforreddit.data.SubredditsContract;

/**
 * Created by sburns on 5/11/16.
 */
public class SubredditsAdapter extends ArrayAdapter<SubredditItem> {

    public SubredditsAdapter(Context context, List<SubredditItem> subredditItems) {
        super(context, 0, subredditItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final SubredditItem subredditItem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_subreddits_list_simple_item, parent, false);
        }

        // Set name
        ((TextView)convertView.findViewById(R.id.subreddit_name)).setText(subredditItem.getName());

        // Set delete
        convertView.findViewById(R.id.remove_subreddit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(String.format(getContext().getString(R.string.remove_subreddit), subredditItem.getName()))
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Remove this subreddit from the list
                                getContext().getContentResolver().delete(
                                        SubredditsContract.Subreddits.buildItemUri(subredditItem.getId()), null, null);
                                SubredditsAdapter.this.remove(subredditItem);
                                SubredditsAdapter.this.notifyDataSetChanged();
                            }
                        });

                builder.show();

            }
        });

        return convertView;
    }
}
