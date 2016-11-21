package dltoy.calpoly.edu.movierecs.Api;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import dltoy.calpoly.edu.movierecs.R;

/**
 * Created by connor on 10/28/16.
 */

public class ImageUtil {
    public static final String STAR_ICON = "\u2b50 ";

    public static void insertImage(String path, int width, ImageView imageView) {
        Picasso.with(imageView.getContext())
                .load(createImageURL(path, width))
                .placeholder(R.drawable.movie_placeholder)
                .into(imageView);
    }

    public static String createImageURL(String path, int width) {
        return "https://image.tmdb.org/t/p/w" + width + path;
    }
}
