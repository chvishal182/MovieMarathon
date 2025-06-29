package com.chelv.moviemarathon.model.dailblackbox;

import com.chelv.moviemarathon.R;
import com.chelv.moviemarathon.util.vault.Secrets;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TMDBMouthPiece {

    private static Retrofit mouthPiece = null;

    public static TMDB_API_Interface getService(){

        if(mouthPiece == null){
            mouthPiece = new Retrofit.Builder()
                    .baseUrl(Secrets.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }

        return mouthPiece.create(TMDB_API_Interface.class);
    }
}
