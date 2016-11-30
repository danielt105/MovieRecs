package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class NotWatchedFragment extends WatchlistBaseFragment {
    protected void getList() {
        movieList = (ArrayList<Movie>) MainActivity.db.getWatchlist(0);
    }

    protected  boolean getInitCheckState() {
        return false;
    }

    protected String toastMessage() {
        return " " + getResources().getString(R.string.sent_to) + " " +
                getResources().getString(R.string.watched_list);
    }
}