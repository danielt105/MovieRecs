package dltoy.calpoly.edu.movierecs.Fragments;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dltoy.calpoly.edu.movierecs.Api.ImageUtil;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.MovieDetailsActivity;
import dltoy.calpoly.edu.movierecs.R;

public class WatchlistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public static final int DEFAULT_IMG_WID = 300;

    private Context context;
    private TextView title;
    private Button switchList;
    private ImageView img;
    public Movie movie;

    public WatchlistViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        title = (TextView) itemView.findViewById(R.id.watchlist_entry_title);
        img = (ImageView) itemView.findViewById(R.id.watchlist_image);
//        switchList = (Button) itemView.findViewById(R.id.switch_list);
//        switchList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                movie.setWatched(!movie.isWatched());
////                int val = MainActivity.db.updateMovie(movie);
////                Log.e("result: ", val + "");
//            }
//        });
        itemView.setOnClickListener(this);
    }

    public void bind(Movie m) {
        movie = m;
        title.setText(m.getTitle());
//        switchList.setText(context.getString(!movie.isWatched() ? R.string.watched : R.string.not_watched));
        ImageUtil.insertImage(m.getImagePath(), DEFAULT_IMG_WID, img);
    }

    @Override
    public void onClick(View v) {
        Intent details = new Intent(v.getContext(), MovieDetailsActivity.class);
        details.putExtra(MovieDetailsActivity.MOVIE_ID_EXTRA, movie.getId());
        v.getContext().startActivity(details);
    }
}
