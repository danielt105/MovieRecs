package dltoy.calpoly.edu.movierecs.Fragments;

import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.ResultList;
import dltoy.calpoly.edu.movierecs.BuildConfig;
import dltoy.calpoly.edu.movierecs.Fragments.advanced_search.AdvancedSearchFragment;
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
    public static final String USE_HORIZONTAL = "USE_HORIZONTAL";
    public static final String SPAN_COUNT = "SPAN_COUNT";
    public static final int DEFAULT_HORIZ_SPAN_COUNT = 1;
    public static final int DEFAULT_VERT_SPAN_COUNT = 2;

    private RecyclerView rv;
    private List<Movie> movies;
    private MovieGridAdapter adapter;
    private int totalPages = 1;
    private SwipeRefreshLayout srf;
    private TextView noResults;
    private TextView goBack;
    private boolean setBack = false;
    private String[] savedSearch;

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
        /*srf = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        srf.setEnabled(false);*/
        //showLoadingIcon(true);
        loadContent(getArguments(), 1);

        Bundle args = getArguments();
        boolean isHorizontal = false;
        if (getArguments().containsKey(USE_HORIZONTAL)) {
            isHorizontal = args.getBoolean(USE_HORIZONTAL);
        }

        adapter.setHorizontal(isHorizontal);

        int spanCount = DEFAULT_VERT_SPAN_COUNT;
        if (getArguments().containsKey(SPAN_COUNT)) {
            spanCount = args.getInt(SPAN_COUNT);
        }

        goBack = (TextView) getView().findViewById(R.id.revise_search);
        if (setBack) {
            goBack.setVisibility(View.VISIBLE);
            goBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).restoreSearch(savedSearch);
                }
            });
        }

        noResults = (TextView) getView().findViewById(R.id.no_results_text);

        rv = (RecyclerView) getView().findViewById(R.id.movie_grid);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(rv.getContext(), spanCount,
                isHorizontal ? GridLayoutManager.HORIZONTAL : GridLayoutManager.VERTICAL, false);
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
        final boolean isH = isHorizontal;
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildLayoutPosition(view);
                int edgeMargin = 16;
                int innerMargin = edgeMargin / 2;


                if (isH) {
                    outRect.left = 0;
                    outRect.right = innerMargin;
                } else {
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
            case QueryType.QUERY_ADV_SEARCH:
                setBack = true;
                String[] params = bundle.getStringArray(QueryType.QUERY_ADV_SEARCH_DATA);
                if (params.length == AdvancedSearchFragment.QUERY_PARAM_COUNT)
                    setUpRequest(MainActivity.apiService.advSearch(
                            BuildConfig.apiKey,
                            page,
                            params[0],
                            params[1].equals("") ? 0 : Integer.parseInt(params[1]),
                            params[2],
                            params[3],
                            params[4],
                            params[5]));
                savedSearch = new String[]{params[0], params[1], params[4], params[5]};
                break;
        }
    }

    private void setUpRequest(Observable<ResultList<Movie>> obs) {
        obs.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultList<Movie>>() {
                    @Override
                    public void onCompleted() {
                        showLoadingIcon(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Grid", "got an error loading movies" + e.getMessage());
                    }

                    @Override
                    public void onNext(ResultList<Movie> movieList) {
                        Log.e("results size: ", movieList.totalResults + "");
                        if (movieList.totalResults == 0) {
                            noResults.setVisibility(View.VISIBLE);
                            noResults.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            rv.setVisibility(View.GONE);
                        }
                        else {
                            totalPages = movieList.totalPages;
                            movies.addAll(movieList.results);
                            adapter.notifyDataSetChanged();
                            showLoadingIcon(false);
                        }
                    }
                });
    }

    public void resetMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
        adapter.notifyDataSetChanged();
    }

    public void showLoadingIcon(boolean show) {
        //srf.setRefreshing(show);
    }
}
