package com.chelv.moviemarathon.model.dailblackbox;

import com.chelv.moviemarathon.model.dataentities.MovieResponse;




import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDB_API_Interface {

    @GET("movie/popular")
    Single<MovieResponse> getMoviesByPage(@Query("page") int page, @Query("api_key") String apiKey);
}
