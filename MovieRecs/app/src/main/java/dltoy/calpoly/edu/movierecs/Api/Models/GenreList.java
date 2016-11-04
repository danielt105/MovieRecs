package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GenreList {
    @SerializedName("genres") public List<Genre> results;
}
