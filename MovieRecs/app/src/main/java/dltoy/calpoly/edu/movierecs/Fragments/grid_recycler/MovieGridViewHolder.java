package dltoy.calpoly.edu.movierecs.Fragments.grid_recycler;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import dltoy.calpoly.edu.movierecs.Api.ImageUtil;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.MovieDetailsActivity;
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

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent details = new Intent(v.getContext(), MovieDetailsActivity.class);
                details.putExtra(MovieDetailsActivity.MOVIE_ID_EXTRA, movie.getId());
                v.getContext().startActivity(details);
            }
        });
    }

    public void bind(Movie m) {
        movie = m;

        ImageUtil.insertImage(movie.getImagePath(), 300, poster);
        title.setText(movie.getTitle());
        rating.setText(ImageUtil.starIcon + Float.toString(movie.getRating()));
    }

    @Override
    public void onClick(View v) {
        Log.d("TEST", "has been clicked");
    }
}
