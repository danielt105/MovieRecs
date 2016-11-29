package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;

public interface WatchlistEntryListener {
    public void onWatchlistEntrySelected(Movie m);
}
