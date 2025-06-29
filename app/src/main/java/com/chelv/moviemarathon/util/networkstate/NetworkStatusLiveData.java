package com.chelv.moviemarathon.util.networkstate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

public class NetworkStatusLiveData extends LiveData<Boolean> {

    private ConnectivityManager connectivityManager;
    private NetworkCallback networkCallback;

    public NetworkStatusLiveData(Context context) {
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkCallback = new NetworkCallback(){
                @Override
                public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        postValue(true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    postValue(false);
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    final boolean hasInternetCapability = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                    postValue(hasInternetCapability);
                }
            };

    }

    @Override
    protected void onActive() {
        super.onActive();
        updateNetworkStatus();
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                                                          .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                                          .build();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void updateNetworkStatus() {
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        if(capabilities != null && capabilities.
                                    hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
            postValue(true);
        }else{
            postValue(false);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        connectivityManager.unregisterNetworkCallback(networkCallback);

    }
}
