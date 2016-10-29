package dltoy.calpoly.edu.movierecs.Fragments;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.MovieList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.Fragments.grid_recycler.MovieGridAdapter;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;
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
    private SwipeRefreshLayout swiperefresh;
    private MovieGridAdapter adapter;

    public GridFragment() {
        movies = new ArrayList<>();
        adapter = new MovieGridAdapter(movies);
        updateMovies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = (RecyclerView) getView().findViewById(R.id.movie_grid);
        rv.setLayoutManager(new GridLayoutManager(rv.getContext(), spanCount, GridLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);

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

        swiperefresh = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMovies();
            }
        });
    }

    private void updateMovies() {
        MainActivity.apiService.getTopRated(BuildConfig.apiKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MovieList>() {
                    @Override
                    public void onCompleted() {
                        swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Grid", "got an error loading movies");
                        swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void onNext(MovieList movieList) {
                        movies.addAll(movieList.results);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}