package dltoy.calpoly.edu.movierecs.Fragments.grid_recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.R;

/**
 * Created by connor on 10/28/16.
 */

public class MovieGridViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private ImageView poster;
    private TextView title;
    private TextView rating;
    private Movie movie;

    public MovieGridViewHolder(View itemView) {
        super(itemView);
        poster = (ImageView) itemView.findViewById(R.id.movie_poster);
        title = (TextView) itemView.findViewById(R.id.movie_title);
        rating = (TextView) itemView.findViewById(R.id.movie_rating);
    }

    public void bind(Movie m) {
        movie = m;

        Picasso.with(poster.getContext()).load(genURL(movie.getImagePath())).into(poster);
        title.setText(movie.getTitle());
        rating.setText("4.0"); // TODO: actually get the rating
    }

    private String genURL(String base) { //TODO:define this url somewhere
        return "https://image.tmdb.org/t/p/" + "w300" + base;
    }

    @Override
    public void onClick(View v) {
        // TODO: launch activity
    }
}
