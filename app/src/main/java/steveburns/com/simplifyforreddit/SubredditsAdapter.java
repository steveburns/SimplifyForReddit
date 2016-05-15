package steveburns.com.simplifyforreddit;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import steveburns.com.simplifyforreddit.data.SubredditsContract;

/**
 * Created by sburns.
 */
public class SubredditsAdapter extends CursorAdapter {

    public SubredditsAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final Long subreddit_id = cursor.getLong(cursor.getColumnIndex(SubredditsContract.Subreddits._ID));
        final String name = cursor.getString(cursor.getColumnIndex(SubredditsContract.Subreddits.NAME));

        // Set name
        ((TextView)view.findViewById(R.id.subreddit_name)).setText(name);

        // Set delete
        view.findViewById(R.id.remove_subreddit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(String.format(context.getString(R.string.remove_subreddit), name))
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Remove this subreddit from the list
                                context.getContentResolver().delete(
                                        SubredditsContract.Subreddits.buildItemUri(subreddit_id), null, null);
                                SubredditsAdapter.this.notifyDataSetChanged();
                            }
                        });

                builder.show();
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_subreddits_list_simple_item, parent, false);
    }
}
