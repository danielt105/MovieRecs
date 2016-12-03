package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.MovieComparators;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.MovieDetailsActivity;
import dltoy.calpoly.edu.movierecs.R;

public abstract class WatchlistBaseFragment extends Fragment  {
    protected WatchlistAdapter adapter;
    protected ArrayList<Movie> movieList;
    protected RecyclerView list;
    protected TextView emptyList;

    private int sorter = 0; //0 default: releasedate, 1: toprated, 2: latest

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.watchlist, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getList();

        adapter = new WatchlistAdapter(getContext(), movieList, getInitCheckState());
        adapter.setWatchListEntryListener(new WatchlistEntryListener() {
            @Override
            public void onWatchlistEntrySelected(Movie m) {
                //find its ndx
                int ndx;
                for (ndx = 0; ndx < movieList.size(); ndx++) {
                    if (m.equals(movieList.get(ndx))) {
                        break;
                    }
                }

                renderSnackbar(m, ndx);

                //remove the movie from the list
                movieList.remove(ndx);
                adapter.notifyItemRemoved(ndx);
                adapter.notifyItemRangeChanged(ndx, movieList.size());
                checkEmptyList();
                sortList();
            }
        });
        list = (RecyclerView)getView().findViewById(R.id.the_list);
        if ((((MainActivity)getActivity()).isSplitPane())) {
            list.setLayoutManager(new GridLayoutManager(list.getContext(), 2, GridLayoutManager.VERTICAL, false));
        }
        else {
            list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }
        list.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(), DividerItemDecoration.VERTICAL);
        list.addItemDecoration(dividerItemDecoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }
        }).attachToRecyclerView(list);

        emptyList = (TextView)getView().findViewById(R.id.empty_list);

        setList();
    }

    private void renderSnackbar(final Movie m, final int ndx) {
        Snackbar snackbar = Snackbar.make(list, m.getTitle() + toastMessage(), Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m.setWatched(!m.isWatched());
                m.setDateAdded(m.getLastDate());
                MainActivity.db.updateMovie(m);
                movieList.add(ndx, m);
                adapter.notifyItemInserted(ndx);
                adapter.notifyItemRangeChanged(ndx, movieList.size());
                checkEmptyList();
                sortList();
            }
        });
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        snackbar.show();
    }

    private void checkEmptyList() {
        if (movieList.size() == 0) {
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(getEmptyListMessage());
        }
        else {
            emptyList.setVisibility(View.GONE);
        }
    }

    private void sortList() {
        if (movieList.size() > 0) {
            switch(sorter) {
                case 1:
                    Collections.sort(movieList, MovieComparators.topRated);
                    break;
                case 2:
                    Collections.sort(movieList, MovieComparators.recent);
                    break;
                default: //default is 0
                    Collections.sort(movieList, MovieComparators.addedToList);
            }
        }
    }

    protected void setList() {
        checkEmptyList();
        sortList();

        adapter.setMovies(movieList);
        adapter.notifyDataSetChanged();
    }

    protected abstract void getList();

    protected abstract boolean getInitCheckState();

    protected abstract String toastMessage();

    protected abstract String getEmptyListMessage();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date_added:
                sorter = 0;
                setList();
                break;
            case R.id.top_rated:
                sorter = 1;
                setList();
                break;
            case R.id.latest:
                sorter = 2;
                setList();
                break;
            default:
                sorter = 0;
                Log.e("Filter Menu", "Couldn't find specified id");
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getList();
        setList();
    }
}
