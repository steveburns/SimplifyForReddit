package steveburns.com.simplifyforreddit;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sburns.
 */
public class SubredditItem implements Parcelable {

    private static final String ID = "id";
    private static final String NAME = "name";

    private Long id;
    private String name;

    public Long getId() { return id; }
    public String getName() { return name; }

    public SubredditItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong(ID, id);
        bundle.putString(NAME, name);
        dest.writeBundle(bundle);
    }

    /**
     * Creator required for class implementing the parcelable interface.
     */
    public final Parcelable.Creator<SubredditItem> CREATOR = new Creator<SubredditItem>() {

        @Override
        public SubredditItem createFromParcel(Parcel source) {
            // read the bundle containing key value pairs from the parcel
            Bundle bundle = source.readBundle();

            // instantiate a person using values from the bundle
            return new SubredditItem(
                    bundle.getLong(ID),
                    bundle.getString(NAME));
        }

        @Override
        public SubredditItem[] newArray(int size) {
            return new SubredditItem[size];
        }

    };
}