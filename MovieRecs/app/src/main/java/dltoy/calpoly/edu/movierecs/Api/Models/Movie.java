package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by main on 10/24/2016.
 */
public class Movie {
    public @SerializedName("id") int id;
    public @SerializedName("title") String title;
    public @SerializedName("poseter_path") String imagePath;
    public @SerializedName("overview") String description;
}
