package dltoy.calpoly.edu.movierecs.Fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.Map;

public class WatchlistPagerAdapter extends FragmentStatePagerAdapter
{
//    private String tabTitles[] = CruApplication.getContext().getResources().getStringArray(R.array.myrides_titles);
    private String tabTitles[] = {"Watched", "Not so watched"}; //TODO: put this into strings.xml
    final int PAGE_COUNT = tabTitles.length;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
//    private Map<Integer, String> fragmentTags;
    private FragmentManager fm;

    public WatchlistPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
//        fragmentTags = new ArrayMap<>();
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
//        Object obj = super.instantiateItem(container, position);
//        if (obj instanceof Fragment) {
//            // record the fragment tag here.
//            Fragment f = (Fragment) obj;
//            String tag = f.getTag();
//            fragmentTags.put(position, tag);
//        }
//        return obj;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

//    public Fragment getFragment(int position) {
//        String tag = fragmentTags.get(position);
//        if (tag == null)
//            return null;
//        return fm.findFragmentByTag(tag);
//    }
}
