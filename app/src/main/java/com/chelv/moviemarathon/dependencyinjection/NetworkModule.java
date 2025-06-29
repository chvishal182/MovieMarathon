package com.chelv.moviemarathon.dependencyinjection;

import com.chelv.moviemarathon.R;
import com.chelv.moviemarathon.model.dailblackbox.TMDB_API_Interface;
import com.chelv.moviemarathon.util.vault.Secrets;

import javax.inject.Singleton;

import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.Module;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public TMDB_API_Interface provideTmdbApiService() {
        Retrofit mouthPiece =
                new Retrofit.Builder()
                .baseUrl((Secrets.BASE_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return mouthPiece.create(TMDB_API_Interface.class);
    }
}
