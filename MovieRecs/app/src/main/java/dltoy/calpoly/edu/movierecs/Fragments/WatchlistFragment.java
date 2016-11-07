package dltoy.calpoly.edu.movierecs.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistFragment extends Fragment  {

//    TabLayout tabLayout;
//    ViewPager viewPager;
//    public static final String TARGET_WATCHLIST_TAB = "watchlist_tab";
//    public static final int[] WATCHLIST_TABS = {0, 1};

    private String[] WATCHLIST_TITLES;
    private int modeWatched; //-1 for not watched, 1, for watched, 0 for both
    private WatchlistAdapter adapter;
    private ArrayList<Movie> watched, notWatched, curList;
    private RecyclerView list;
//    private TextView title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.watchlist, container, false);
//        return inflater.inflate(R.layout.watchlist_tabs, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.watchlist_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WATCHLIST_TITLES = getResources().getStringArray(R.array.watchlist_titles);
        getLists();

        curList = new ArrayList<>();
        adapter = new WatchlistAdapter(curList);
        modeWatched = 1;
        list = (RecyclerView)getView().findViewById(R.id.the_list);
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(adapter);
//        list.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Movie m = modeWatched ? watched.get(list.getChildLayoutPosition(v)) :
//                        notWatched.get(list.getChildLayoutPosition(v));
//                displayAlertDialog(v.getContext(), m);
//                return true;
//            }
//        });

//        title = (TextView) getView().findViewById(R.id.watchlist_title);
//        title.setTextSize(35); //TODO: need to be able to set this dynamically
        setList();
    }

    private void setList() {
        for (Movie m : curList)
            Log.e("curlist contents", m.getTitle());
        if (modeWatched < 0)
            curList = notWatched;
        else if (modeWatched > 0)
            curList = watched;
        else {
            curList = new ArrayList<>();
            curList.addAll(watched);
            curList.addAll(notWatched);
        }
        adapter.setMovies(curList);
        adapter.notifyDataSetChanged();
        for (Movie m : curList)
            Log.e("curlist contents after", m.getTitle());

//        title.setText(WATCHLIST_TITLES[modeWatched + 1]);
    }

    private void getLists() {
        watched = (ArrayList<Movie>) MainActivity.db.getWatchlist(1);
        notWatched = (ArrayList<Movie>) MainActivity.db.getWatchlist(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int oldMode = -2;
        switch (item.getItemId()) {
            case R.id.watched_list:
                oldMode = modeWatched;
                modeWatched = 1;
                break;
            case R.id.not_watched_list:
                oldMode = modeWatched;
                modeWatched = -1;
                break;
            case R.id.both_lists:
                oldMode = modeWatched;
                modeWatched = 0;
                break;
            default:
                Log.e("Watchlist Menu", "Couldn't find specified id");
        }
        //if we really do need to reload things
        if (oldMode != modeWatched) {
            getLists();
            setList();
            ((MainActivity) getActivity()).setToolbarText(
                    getResources().getStringArray(R.array.watchlist_titles)[modeWatched + 1]);
        }
        return true;
    }

//    private void displayAlertDialog(Context c, final Movie m) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
//        alertDialogBuilder.setTitle(getResources().getString(R.string.alert_dialog_title));
//
//        // set dialog message
//        alertDialogBuilder
//                .setMessage(getResources().getString(R.string.alert_dialog_text) + ": " +
//                        m.getTitle() + "?")
//                .setPositiveButton(getResources().getString(R.string.remove),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                MainActivity.db.deleteMovie(m);
//                                adapter.notifyDataSetChanged();
//                            }
//                        })
//                .setNegativeButton(getResources().getString(R.string.cancel),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,int id) {
//                                dialog.cancel();
//                            }
//                        });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }

//
//        tabLayout = (TabLayout) getView().findViewById(R.id.tabs);
//        tabLayout.setTabTextColors(ContextCompat.getColorStateList(getContext(),
//                ((MainActivity)getActivity()).getTextColor()));
//        viewPager = (ViewPager) getView().findViewById(R.id.viewpager);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                WatchedFragment frag = (WatchedFragment) ((WatchlistPagerAdapter)viewPager.getAdapter()).getFragment(position);
//                if (frag != null) {
//                    frag.updateList();
//                    Log.e("page scrolled", position + " ");
//                }
//                Fragment page = getActivity().getSupportFragmentManager().
//                        findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + viewPager.getCurrentItem());
//                // based on the current position you can then cast the page to the correct
//                // class and call the method:
//                Log.e("page scrolled", position + " ");
//                if (viewPager.getCurrentItem() == 0 && page != null) {
////                    ((WatchedFragment)page).updateList("new item");
//                    ((WatchedFragment)page).updateList();
//                }
//            }

//            @Override
//            public void onPageSelected(int position) {
//                WatchedFragment frag = (WatchedFragment)
//                        ((WatchlistPagerAdapter)viewPager.getAdapter()).getRegisteredFragment(position);
//                if (frag != null) {
//                    frag.updateList();
//                    Log.e("page selected", position + " ");
//                    ((WatchlistPagerAdapter)viewPager.getAdapter()).notifyDataSetChanged();
//                }
//            }

//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.e("page scrolled state", state + " ");
//            }
//        });
//    }

//    public void switchToTab()
//    {
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            int ndx = bundle.getInt(TARGET_WATCHLIST_TAB);
//            tabLayout.getTabAt(ndx).select();
////            WatchedFragment frag = (WatchedFragment) ((WatchlistPagerAdapter)viewPager.getAdapter()).getFragment(ndx);
////            if (frag != null) {
////                frag.updateList();
////                Log.e("tab switched", ndx + " ");
////            }
//        }
//    }

//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        if(viewPager.getAdapter() == null)
//        {
//            viewPager.setAdapter(new WatchlistPagerAdapter(getChildFragmentManager()));
//            tabLayout.setupWithViewPager(viewPager);
//            switchToTab();
//        }
//    }
}
