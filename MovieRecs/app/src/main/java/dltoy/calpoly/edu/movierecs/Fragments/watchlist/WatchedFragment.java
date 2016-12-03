package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchedFragment extends WatchlistBaseFragment {
    protected void getList() {
        movieList = (ArrayList<Movie>) MainActivity.db.getWatchlist(1);
    }

    protected  boolean getInitCheckState() {
        return true;
    }

    protected String toastMessage() {
        return getResources().getString(R.string.sent_to) + " " +
                getResources().getString(R.string.not_watched_list);
    }

    protected String getEmptyListMessage() {
        return getResources().getString(R.string.empty_list);
    }
}
