package dltoy.calpoly.edu.movierecs.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class WatchlistNotWatchedFragment extends WatchedFragment {

    public WatchlistNotWatchedFragment() {
        super();
    }

    protected void updateList() {
        Log.e("Not watched", "update list called");
        movies = (ArrayList<Movie>) MainActivity.db.getWatchlist(0);
        for (Movie m : movies) {
            Log.e("name", m.getTitle());
        }
    }
}
