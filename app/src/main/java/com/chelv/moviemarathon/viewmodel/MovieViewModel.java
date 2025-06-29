package com.chelv.moviemarathon.viewmodel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.chelv.moviemarathon.model.dailblackbox.TMDB_API_Interface;
import com.chelv.moviemarathon.model.dataentities.Movie;
import com.chelv.moviemarathon.model.paging.MoviePagingSource;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

@HiltViewModel
public class MovieViewModel extends ViewModel {
    
    public Flowable<PagingData<Movie>> pagingDataFlowable;
    private TMDB_API_Interface apiService;

    @Inject
    public MovieViewModel(TMDB_API_Interface apiService) {
        this.apiService = apiService;
        init();
    }



    public MovieViewModel(){
        init();
    }



    private void init() {

        Pager<Integer, Movie> moviePager = new Pager<>(
                            new PagingConfig(
                                    20,
                                    20,
                                    false,
                                    20,
                                    60)
                                        ,() -> new MoviePagingSource(apiService));


        pagingDataFlowable = PagingRx.getFlowable(moviePager);
        CoroutineScope coroutineScope = ViewModelKt.getViewModelScope(this);
        PagingRx.cachedIn(pagingDataFlowable, coroutineScope);
    }

}
