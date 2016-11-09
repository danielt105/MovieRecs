package dltoy.calpoly.edu.movierecs.Api;

import dltoy.calpoly.edu.movierecs.Api.Models.GenreList;
import dltoy.calpoly.edu.movierecs.Api.Models.Keyword;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.Api.Models.ResultList;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface MovieApi {

    @GET("movie/top_rated")
    Observable<ResultList<Movie>> getTopRated(@Query("api_key") String key);

    @GET("movie/top_rated")
    Observable<ResultList<Movie>> getTopRated(@Query("api_key") String key, @Query("page") int number);

    @GET("movie/{id}")
    Observable<Movie> getById(@Path("id") int id, @Query("api_key") String key);

    @GET("movie/{id}/recommendations")
    Observable<ResultList<Movie>> getRecommendations(@Path("id") int id, @Query("api_key") String key,
                                             @Query("page") int page);

    @GET("search/movie")
    Observable<ResultList<Movie>> searchByTitle(@Query("api_key") String key, @Query("query") String query);

    // super janky way of doing this but... we're including the query after the key
    @GET("discover/movie")
    Observable<ResultList<Movie>> advSearch(@Query("api_key") String key, @Query("page") int page);

    @GET("genre/movie/list")
    Observable<GenreList> getGenres(@Query("api_key") String key);

    @GET("search/keyword")
    Observable<ResultList<Keyword>> searchKeyword(@Query("api_key") String key, @Query("query") String query);

//    @GET("search/person")
//    Observable<ResultList<Person>> searchPerson(@Query("api_key") String key, @Query("query") String query);
}
