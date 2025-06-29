package com.chelv.moviemarathon.adapter.data_adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.chelv.moviemarathon.R;
import com.chelv.moviemarathon.databinding.MovieItemBinding;
import com.chelv.moviemarathon.model.dataentities.Movie;
import com.chelv.moviemarathon.util.vault.Secrets;

import javax.inject.Inject;


import kotlin.coroutines.CoroutineContext;

public class MoviesAdapter extends PagingDataAdapter<Movie, MoviesAdapter.MovieViewHolder> {

    public static final int LOADING_ITEM = 0;
    public static final int MOVIE_ITEM   = 1;

    RequestManager glide;

    public static final DiffUtil.ItemCallback<Movie> MOVIE_DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Inject
    public MoviesAdapter(RequestManager glide) {
        super(MOVIE_DIFF_CALLBACK);
        this.glide = glide;
    }

    public MoviesAdapter() {
        super(MOVIE_DIFF_CALLBACK);
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() ? MOVIE_ITEM : LOADING_ITEM;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieItemBinding movieItemBinding = MovieItemBinding
                                                .inflate(LayoutInflater.from(parent.getContext()),
                                                            parent,
                                                false);
        return new MovieViewHolder(movieItemBinding);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
            Movie currentMovie = getItem(position);

            if(currentMovie != null){
                    glide.load(String.format("%s%s", Secrets.IMAGE_BASE_URL, currentMovie.getPosterPath()))
                         .into(holder.movieItemBinding.imageViewMovie);

                    holder.movieItemBinding.textViewRating.setText(String.valueOf(currentMovie.getVoteAverage()));

            }

    }



    protected static class MovieViewHolder extends RecyclerView.ViewHolder{
        private MovieItemBinding movieItemBinding;

        public MovieViewHolder(@NonNull MovieItemBinding movieItemBinding) {
            super(movieItemBinding.getRoot());
            this.movieItemBinding = movieItemBinding;
        }


    }
}
