package dltoy.calpoly.edu.movierecs.Api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by main on 10/24/2016.
 */
public interface MovieApi {
    @GET("/")
    Call<Movie> searchByTitle(@Query("t") String title);
}
