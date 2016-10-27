package dltoy.calpoly.edu.movierecs.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class WatchlistPagerAdapter extends FragmentStatePagerAdapter
{
//    private String tabTitles[] = CruApplication.getContext().getResources().getStringArray(R.array.myrides_titles);
    private String tabTitles[] = {"Watched", "Not so watched"}; //TODO: put this into strings.xml
    final int PAGE_COUNT = tabTitles.length;

    public WatchlistPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag = null;

        switch (position) {
            case 0:
                frag = new WatchlistWatchedFragment();
                break;
            case 1:
                frag = new WatchlistNotWatchedFragment();
                break;
        }

        return frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
