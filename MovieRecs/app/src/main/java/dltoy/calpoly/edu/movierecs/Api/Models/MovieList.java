package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;

/**
 * Created by main on 10/25/2016.
 */
public class MovieList {
    @SerializedName("results") public List<Movie> results;
    @SerializedName("page") public int page;
    @SerializedName("total_results") public int totalResults;
    @SerializedName("total_pages") public int totalPages;
}
