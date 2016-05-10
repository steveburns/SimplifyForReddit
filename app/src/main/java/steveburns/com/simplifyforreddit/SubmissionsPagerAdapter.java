package steveburns.com.simplifyforreddit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by sburns on 5/10/16.
 */
public class SubmissionsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = SubmissionsPagerAdapter.class.getSimpleName();

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Log.d(TAG, "destroyItem called");
    }

    /*
        * Some helpful website:
        * https://www.javacodegeeks.com/2013/04/android-tutorial-using-the-viewpager.html
        *
        * Remember you might have to call: notifyDataSetChanged
        * see: http://stackoverflow.com/questions/8060904/add-delete-pages-to-viewpager-dynamically
        *
        * How to prevent scrolling to left or right:
        * http://stackoverflow.com/questions/13270425/control-which-directions-the-viewpager-can-be-scrolled-dynamically-with-ui-feed
        * !!! ONE WAY TO DO THIS CHEAPLY (MAYBE) IS TO KEEP TRACK OF THE CURRENT PAGE INDEX AND NOT LET THE USER GO BACK.
        *
        * Might have to use FragmentStatePagerAdapter:
        *
        *
        * */
    public SubmissionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        Log.d(TAG, "getCount called");
        return Integer.MAX_VALUE;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem called");
        return new MainActivityFragment();
    }
}
