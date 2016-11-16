package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;

public class WatchedFragment extends WatchlistBaseFragment {
    protected void getList() {
        movieList = (ArrayList<Movie>) MainActivity.db.getWatchlist(1);
    }
}
