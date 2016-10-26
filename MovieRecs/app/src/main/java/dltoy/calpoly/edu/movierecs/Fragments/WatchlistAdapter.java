package dltoy.calpoly.edu.movierecs.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistViewHolder> {
    private ArrayList<Movie> movies;

    public WatchlistAdapter(ArrayList<Movie> entries) {
            this.movies = entries;
            }

    @Override
    public int getItemViewType(int position) {
            return R.layout.watchlist_entry;
    }

    @Override
    public WatchlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WatchlistViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(WatchlistViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
