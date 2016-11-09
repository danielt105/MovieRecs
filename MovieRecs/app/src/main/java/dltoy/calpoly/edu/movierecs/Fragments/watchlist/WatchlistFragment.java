package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

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
import dltoy.calpoly.edu.movierecs.Fragments.watchlist.WatchlistAdapter;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistFragment extends Fragment  {
    private String[] WATCHLIST_TITLES;
    private int modeWatched; //-1 for not watched, 1, for watched, 0 for both
    private WatchlistAdapter adapter;
    private ArrayList<Movie> watched, notWatched, curList;
    private RecyclerView list;

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
}
