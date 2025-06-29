package com.chelv.moviemarathon.model.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.chelv.moviemarathon.R;
import com.chelv.moviemarathon.model.dailblackbox.TMDBMouthPiece;
import com.chelv.moviemarathon.model.dailblackbox.TMDB_API_Interface;
import com.chelv.moviemarathon.model.dataentities.Movie;
import com.chelv.moviemarathon.model.dataentities.MovieResponse;
import com.chelv.moviemarathon.util.vault.Secrets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, Movie> {

    private final TMDB_API_Interface tmdbApiInterface;
    private final String apiKey;

    public MoviePagingSource() {
        this.tmdbApiInterface = TMDBMouthPiece.getService();
        this.apiKey           = Secrets.API_KEY;
    }

    public MoviePagingSource(TMDB_API_Interface tmdbApiInterface, String apiKey) {
        this.tmdbApiInterface = tmdbApiInterface;
        this.apiKey = apiKey;
    }

    public  MoviePagingSource(TMDB_API_Interface apiService) {

        this.tmdbApiInterface = apiService;
        apiKey = Secrets.API_KEY;
    }


    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(LoadParams<Integer> loadParams) {
        int pageNumber = (loadParams.getKey() != null) ? loadParams.getKey() : 1;

        return tmdbApiInterface.getMoviesByPage(pageNumber, apiKey)
                .subscribeOn(Schedulers.io()).map(new Function<MovieResponse, LoadResult<Integer, Movie>>() {
                    @Override
                    public LoadResult<Integer, Movie> apply(MovieResponse movieResponse) throws Throwable {
                        List<Movie> movies = movieResponse.getMovies();

                        if (movies == null) {
                            movies = Collections.emptyList(); // Use an empty list if movies are null
                        }

                        Integer prevKey = (pageNumber == 1) ? null : pageNumber - 1;
                        // Determine nextKey: null if movies list is empty or we are at/past totalPages
                        Integer nextKey = (movies.isEmpty() || pageNumber >= movieResponse.getTotalPages())
                                ? null : pageNumber + 1;

                        return new LoadResult.Page<>(movies, prevKey, nextKey);

                    }
                }).onErrorReturn(new Function<Throwable, LoadResult<Integer, Movie>>() {
                    @Override
                    public LoadResult<Integer, Movie> apply(Throwable throwable) throws Throwable {
                        return new LoadResult.Error<>(throwable);
                    }
                });
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Movie> pagingState) {

        //Getting the last item the user was looking at
        Integer anchorPosition = pagingState.getAnchorPosition();

        if(anchorPosition == null){
            return null;
        }

        //Page that contains the anchor position
        LoadResult.Page<Integer, Movie> closetPage = pagingState.closestPageToPosition(anchorPosition);
        if(closetPage == null) return null;

        Integer prevKey = closetPage.getPrevKey();
        if(prevKey != null){
            return prevKey+ 1;
        }

        Integer nextKey = closetPage.getNextKey();
        if(nextKey != null){
            return nextKey - 1;
        }
        return null;
    }
}
