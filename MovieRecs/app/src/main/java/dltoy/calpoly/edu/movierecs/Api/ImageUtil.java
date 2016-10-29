package dltoy.calpoly.edu.movierecs.Api;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by connor on 10/28/16.
 */

public class ImageUtil {
    public static final String starIcon = "\u2b50 ";
    public static void insertImage(String path, int width, ImageView imageView) {
        Picasso.with(imageView.getContext()).load(createImageURL(path, width)).into(imageView);
    }

    public static String createImageURL(String path, int width) {
        return "https://image.tmdb.org/t/p/w" + width + path;
    }
}
