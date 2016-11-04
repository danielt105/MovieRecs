package dltoy.calpoly.edu.movierecs.Fragments;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.MovieList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.EndlessScrollListener;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.QueryType;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.MovieGridAdapter;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by connor on 10/28/16.
 */

public class GridFragment extends Fragment {
    private int spanCount = 2;
    private RecyclerView rv;
    private List<Movie> movies;
    private MovieGridAdapter adapter;
    private int totalPages = 1;

    public GridFragment() {
        movies = new ArrayList<>();
        adapter = new MovieGridAdapter(movies);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadContent(getArguments(), 1);

        rv = (RecyclerView) getView().findViewById(R.id.movie_grid);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(rv.getContext(), spanCount, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(adapter);

        EndlessScrollListener endlessScroll = new EndlessScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (page < totalPages) {
                    loadContent(getArguments(), page);
                }
            }
        };

        rv.addOnScrollListener(endlessScroll);

        // set the margins on each grid item
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildLayoutPosition(view);
                int edgeMargin = 16;
                int innerMargin = edgeMargin / 2;

                if (pos % 2 == 0) {
                    outRect.left = edgeMargin;
                    outRect.right = innerMargin;
                } else {
                    outRect.right = edgeMargin;
                    outRect.left = innerMargin;
                }

                // check for the very top row or very bottom row which are edges
                if (pos == 0 || pos == 1) {
                    outRect.top = edgeMargin;
                    outRect.bottom = innerMargin;
                } else if (pos == movies.size() - 1 || pos == movies.size() - 2) {
                    outRect.bottom = edgeMargin;
                    outRect.top = innerMargin;
                } else {
                    outRect.top = innerMargin;
                    outRect.bottom = innerMargin;
                }
            }
        });
    }

    private void loadContent(Bundle bundle, int page) {
        int type = (int) bundle.get(QueryType.QUERY_TYPE);

        switch (type) {
            case QueryType.QUERY_TOP_RATED:
                setUpRequest(MainActivity.apiService.getTopRated(BuildConfig.apiKey, page));
                break;
            case QueryType.QUERY_SEARCH:
                setUpRequest(MainActivity.apiService.searchByTitle(BuildConfig.apiKey,
                        (String) bundle.get(QueryType.QUERY_SEARCH_VALUE)));
                break;
            case QueryType.QUERY_RECS:
                setUpRequest(MainActivity.apiService.getRecommendations(
                        (int) bundle.get(QueryType.QUERY_MOVIE_ID),
                        BuildConfig.apiKey,
                        page));
                break;
        }
    }

    private void setUpRequest(Observable<MovieList> obs) {
        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieList>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Grid", "got an error loading movies");
                    }

                    @Override
                    public void onNext(MovieList movieList) {
                        totalPages = movieList.totalPages;
                        movies.addAll(movieList.results);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
