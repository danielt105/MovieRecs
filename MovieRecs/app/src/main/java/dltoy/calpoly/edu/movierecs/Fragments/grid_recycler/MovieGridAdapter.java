package dltoy.calpoly.edu.movierecs.Fragments.grid_recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.R;

/**
 * Created by connor on 10/28/16.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridViewHolder> {
    private List<Movie> movies;

    public MovieGridAdapter(List<Movie> entries) {
        this.movies = entries;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.grid_tile;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MovieGridViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
}
