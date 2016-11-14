package dltoy.calpoly.edu.movierecs.Fragments.grid_recycler;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.R;

/**
 * Created by connor on 10/28/16.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridViewHolder> {
    private List<Movie> movies;
    private boolean isHorizontal;

    public MovieGridAdapter(List<Movie> entries) {
        this.movies = entries;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.grid_tile;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);

        if (isHorizontal) {
            v.setLayoutParams(new FrameLayout.LayoutParams((int)(parent.getMeasuredWidth() / 3.5), FrameLayout.LayoutParams.WRAP_CONTENT));
        }

        return new MovieGridViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }
}
