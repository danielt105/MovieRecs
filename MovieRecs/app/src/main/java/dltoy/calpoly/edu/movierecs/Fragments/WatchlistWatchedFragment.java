package dltoy.calpoly.edu.movierecs.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistWatchedFragment extends WatchedFragment {

    public WatchlistWatchedFragment() {
        super();
    }

    public void updateList() {
        Log.e("watched watched", "update list called");
        movies = (ArrayList<Movie>) MainActivity.db.getWatchlist(1);
        for (Movie m : movies) {
            Log.e("name", m.getTitle());
        }
    }
}
