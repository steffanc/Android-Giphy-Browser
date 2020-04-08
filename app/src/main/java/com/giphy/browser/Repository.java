package com.giphy.browser;

import androidx.annotation.NonNull;

import com.giphy.browser.model.Gifs;
import com.giphy.browser.model.Resource;
import com.giphy.browser.network.ApiException;
import com.giphy.browser.network.NetworkException;
import com.giphy.browser.network.Service;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

public class Repository {
    @NonNull
    private final String apiKey;
    @NonNull
    private final Service service;

    public Repository(@NonNull String apiKey, @NonNull Service service) {
        this.apiKey = apiKey;
        this.service = service;
    }

    @NonNull
    public Single<Resource<Gifs>> getTrendingGifs(int offset) {
        return service.getTrendingGifs(apiKey, offset)
                .subscribeOn(Schedulers.io())
                .map(this::toResource);
    }

    @NonNull
    public Single<Resource<Gifs>> getSearchGifs(@NonNull String query, int offset) {
        return service.getSearchGifs(apiKey, query, offset)
                .subscribeOn(Schedulers.io())
                .map(this::toResource);
    }

    @NonNull
    private <T> Resource<T> toResource(@NonNull Result<T> result) {
        if (result.isError()) {
            // Network error
            if (result.error() instanceof IOException) {
                return Resource.failure(new NetworkException(result.error()));
            }
            // Programmer error
            throw new RuntimeException(result.error());
        } else {
            final Response<T> response = Objects.requireNonNull(result.response());
            if (response.isSuccessful()) {
                return Resource.success(response.body());
            }
            // API error
            return Resource.failure(new ApiException(response.message()));
        }
    }
}
