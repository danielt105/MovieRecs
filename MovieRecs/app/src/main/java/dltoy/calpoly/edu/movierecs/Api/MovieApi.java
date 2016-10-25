package dltoy.calpoly.edu.movierecs.Api;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.MovieList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApi {

    @GET("movie/top_rated")
    Call<Movie> getTopRated(@Query("api_key") String key);

    @GET("search/movie")
    Call<MovieList> searchByTitle(@Query("api_key") String key, @Query("query") String query);

//    @GET("/genre/movie/list")
//    Call<Genre> getGenres(@Query("api_key") String key);
}
