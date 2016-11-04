package dltoy.calpoly.edu.movierecs.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public abstract class WatchedFragment extends Fragment {

    protected RecyclerView list;
    protected WatchlistAdapter adapter;
    protected ArrayList<Movie> movies;

    public WatchedFragment() {
        updateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.watchlist_not_watched, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new WatchlistAdapter(movies);
        list = (RecyclerView)getView().findViewById(R.id.the_list);
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        list.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int index = viewHolder.getAdapterPosition();
                Log.e("you swiped", "no swiping!");
                movies.get(index).setWatched(!movies.get(index).isWatched());
                MainActivity.db.updateMovie(movies.get(index));
                movies.remove(index);
                list.removeViewAt(index);
                adapter.notifyItemRemoved(index);
                adapter.notifyItemRangeChanged(index, movies.size());
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(list);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            if (adapter != null) {
                updateList();
                adapter = new WatchlistAdapter(movies);
                list.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        }
    }

    abstract void updateList();
}
