package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KeywordList {
    @SerializedName("results") public List<Keyword> results;
    @SerializedName("page") public int page;
    @SerializedName("total_results") public int totalResults;
    @SerializedName("total_pages") public int totalPages;
}
