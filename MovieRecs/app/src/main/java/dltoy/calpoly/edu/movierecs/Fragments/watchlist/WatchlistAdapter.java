package dltoy.calpoly.edu.movierecs.Fragments.watchlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dltoy.calpoly.edu.movierecs.Api.ImageUtil;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Constants;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.MovieDetailsActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {
    private ArrayList<Movie> movies;
    private Context context;

    public WatchlistAdapter(Context context, ArrayList<Movie> entries) {
        this.context = context;
        this.movies = entries;
    }

    @Override
    public int getItemViewType(int position) {
            return R.layout.watchlist_entry;
    }

    @Override
    public WatchlistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WatchlistViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(WatchlistViewHolder holder, int position) {
        holder.bind(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(ArrayList<Movie> newEntries) {
        movies = newEntries;
    }

    public class WatchlistViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnLongClickListener {

        private Context context;
        private TextView title;
        private TextView rating;
        private TextView desc;
        private CheckBox switchList;
        private ImageView img;
        public Movie movie;
        private boolean cheat = true; //toggle this off when switching between this

        public WatchlistViewHolder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.watchlist_entry_title);
            rating = (TextView) itemView.findViewById(R.id.watchlist_entry_rating);
            desc = (TextView) itemView.findViewById(R.id.watchlist_entry_description);
            img = (ImageView) itemView.findViewById(R.id.watchlist_image);
            switchList = (CheckBox) itemView.findViewById(R.id.watchlist_entry_checkbox);
            switchList.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(this);

            //resize stuff if its in on the tablet -_-
            if (MainActivity.isSplitPane()) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)desc.getLayoutParams();
                params.setMargins(0, Constants.WATCHLIST_DESC_SPACE, 0, 0);
                desc.setLayoutParams(params);
                desc.setMaxLines(Constants.WATCHLIST_DESC_WIDE_MAX_LINE);
                int wid = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        Constants.IMG_WID_DP, context.getResources().getDisplayMetrics());
                img.setLayoutParams(new LinearLayout.LayoutParams(wid,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }

        public void bind(Movie m) {
            movie = m;
            title.setText(m.getTitle());
            rating.setText(m.getRating() + "");
            desc.setText(m.getDescription());
            cheat = false;
            switchList.setChecked(m.isWatched());
            cheat = true;
            ImageUtil.insertImage(m.getImagePath(), Constants.DEFAULT_IMG_WID, img);
        }

        @Override
        public void onClick(View v) {
            Intent details = new Intent(v.getContext(), MovieDetailsActivity.class);
            details.putExtra(MovieDetailsActivity.MOVIE_ID_EXTRA, movie.getId());
            details.putExtra(Constants.CUR_FRAG_KEY, R.id.watchlist);
            ((Activity)context).startActivityForResult(details, Constants.PREV_FRAG_KEY);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (cheat) {
    //            Log.e("check changed", movie.getTitle() + " set to " + isChecked);
                movie.setWatched(isChecked);
                MainActivity.db.updateMovie(movie);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            displayAlertDialog(view.getContext());
            return true;
        }

        private void displayAlertDialog(Context c) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
            alertDialogBuilder.setTitle(context.getResources().getString(R.string.alert_dialog_title));

            // set dialog message
            alertDialogBuilder
                    .setMessage(context.getResources().getString(R.string.alert_dialog_text) + ": " +
                            movie.getTitle() + "?")
                    .setPositiveButton(context.getResources().getString(R.string.remove),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    MainActivity.db.deleteMovie(movie);
                                    movies.remove(movie);
                                    notifyDataSetChanged();
                                }
                            })
                    .setNegativeButton(context.getResources().getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
