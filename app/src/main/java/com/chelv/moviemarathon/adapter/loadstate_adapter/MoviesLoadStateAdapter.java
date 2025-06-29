package com.chelv.moviemarathon.adapter.loadstate_adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.chelv.moviemarathon.R;
import com.chelv.moviemarathon.databinding.LoadStateItemBinding;

public class MoviesLoadStateAdapter extends LoadStateAdapter<MoviesLoadStateAdapter.MovieLoadStateViewHolder> {

    private View.OnClickListener movieRetryCallback;

    public MoviesLoadStateAdapter(View.OnClickListener movieRetryCallback){
        this.movieRetryCallback = movieRetryCallback;
    }

    @Override
    public void onBindViewHolder(MovieLoadStateViewHolder movieLoadStateViewHolder, LoadState loadState) {
        movieLoadStateViewHolder.bind(loadState);
    }


    @NonNull
    @Override
    public MovieLoadStateViewHolder onCreateViewHolder(ViewGroup parent, LoadState loadState) {
        return new MovieLoadStateViewHolder(parent, movieRetryCallback);
    }

    protected class MovieLoadStateViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar movieProgressBar;
        private TextView movieErrorMessage;
        private Button movieRetry;

        public MovieLoadStateViewHolder(@NonNull ViewGroup parent,
                                        @NonNull View.OnClickListener retryCallback){
            super(LayoutInflater.from(parent.getContext())
                                .inflate(
                                        R.layout.load_state_item,
                                        parent,
                                        false
                                )
            );

            LoadStateItemBinding loadStateItemBinding = LoadStateItemBinding.bind(itemView);
            movieProgressBar  = loadStateItemBinding.progressBar;
            movieErrorMessage = loadStateItemBinding.errorMessage;
            movieRetry        = loadStateItemBinding.retryButton;
            movieRetry.setOnClickListener(retryCallback);
        }

        public void bind(LoadState loadState){

            if(loadState instanceof LoadState.Error){
                LoadState.Error loadStateError = (LoadState.Error) loadState;
                movieErrorMessage.setText(loadStateError.getError().getLocalizedMessage());
            }

            movieProgressBar.setVisibility(
                    loadState instanceof LoadState.Loading ? View.VISIBLE : View.GONE
            );

            movieRetry.setVisibility(
                    loadState instanceof LoadState.Error ? View.VISIBLE : View.GONE
            );

            movieErrorMessage.setVisibility(
                    loadState instanceof LoadState.Error ? View.VISIBLE : View.GONE
            );


        }
    }
}
