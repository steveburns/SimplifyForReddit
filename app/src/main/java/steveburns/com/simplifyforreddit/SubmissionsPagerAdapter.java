package steveburns.com.simplifyforreddit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by sburns on 5/10/16.
 */
public class SubmissionsPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = SubmissionsPagerAdapter.class.getSimpleName();

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        Log.d(TAG, "destroyItem called");
    }

    /*
        * Some helpful websites I used went setting up the ViewPager
        * https://www.javacodegeeks.com/2013/04/android-tutorial-using-the-viewpager.html
        *
        * General, simple ViewPager example:
        * http://developer.android.com/reference/android/support/v4/view/ViewPager.html
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
