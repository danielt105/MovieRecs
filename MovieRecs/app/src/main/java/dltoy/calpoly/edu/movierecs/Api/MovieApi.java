package dltoy.calpoly.edu.movierecs.Api;

import dltoy.calpoly.edu.movierecs.Api.Models.GenreList;
import dltoy.calpoly.edu.movierecs.Api.Models.KeywordList;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.MovieList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieApi {

    @GET("movie/top_rated")
    Observable<MovieList> getTopRated(@Query("api_key") String key);

    @GET("movie/top_rated")
    Observable<MovieList> getTopRated(@Query("api_key") String key, @Query("page") int number);

    @GET("movie/{id}")
    Observable<Movie> getById(@Path("id") int id, @Query("api_key") String key);

    @GET("movie/{id}/recommendations")
    Observable<MovieList> getRecommendations(@Path("id") int id, @Query("api_key") String key,
                                             @Query("page") int page);

    @GET("search/movie")
    Observable<MovieList> searchByTitle(@Query("api_key") String key, @Query("query") String query);

    // super janky way of doing this but... we're including the query after the key
    @GET("discover/movie")
    Observable<MovieList> advSearch(@Query("api_key") String key, @Query("page") int page);

    @GET("genre/movie/list")
    Observable<GenreList> getGenres(@Query("api_key") String key);

    @GET("search/keyword")
    Observable<KeywordList> searchKeyword(@Query("api_key") String key, @Query("query") String query);
}
