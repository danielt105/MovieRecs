package dltoy.calpoly.edu.movierecs.Fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistFragment extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    public static final String TARGET_WATCHLIST_TAB = "watchlist_tab";
    public static final int[] WATCHLIST_TABS = {0, 1};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.watchlist_tabs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getContext(),
                ((MainActivity)getActivity()).getTextColor()));
        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
    }

    public void switchToTab()
    {
        Bundle bundle = getArguments();
        if (bundle != null)
            tabLayout.getTabAt(bundle.getInt(TARGET_WATCHLIST_TAB)).select();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(viewPager.getAdapter() == null)
        {
            viewPager.setAdapter(new WatchlistPagerAdapter(getChildFragmentManager()));
            tabLayout.setupWithViewPager(viewPager);
            switchToTab();
        }
    }
}
