package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
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
import java.util.Collections;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.MovieComparators;
import dltoy.calpoly.edu.movierecs.Fragments.watchlist.WatchlistAdapter;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public abstract class WatchlistBaseFragment extends Fragment  {
    protected WatchlistAdapter adapter;
    protected ArrayList<Movie> movieList;
    protected RecyclerView list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.watchlist, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getList();

        adapter = new WatchlistAdapter(getContext(), movieList);
        list = (RecyclerView)getView().findViewById(R.id.the_list);
        if ((((MainActivity)getActivity()).isSplitPane())) {
            list.setLayoutManager(new GridLayoutManager(list.getContext(), 2, GridLayoutManager.VERTICAL, false));
        }
        else {
            list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
        list.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);

        setList();
    }

    protected void setList() {
        adapter.setMovies(movieList);
        adapter.notifyDataSetChanged();
    }

    protected abstract void getList();


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_rated:
                Collections.sort(movieList, MovieComparators.topRated);
                setList();
                break;
            case R.id.latest:
                Collections.sort(movieList, MovieComparators.recent);
                setList();
                break;
            default:
                Log.e("Filter Menu", "Couldn't find specified id");
        }
        return true;
    }
}
