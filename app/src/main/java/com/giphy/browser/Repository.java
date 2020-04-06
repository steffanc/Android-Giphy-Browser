package com.giphy.browser;

import com.giphy.browser.model.Gifs;
import com.giphy.browser.model.Resource;
import com.giphy.browser.network.ApiException;
import com.giphy.browser.network.NetworkException;
import com.giphy.browser.network.Service;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.adapter.rxjava2.Result;

public class Repository {
    private final String apiKey;
    private final Service service;

    public Repository(@NotNull String apiKey, @NotNull Service service) {
        this.apiKey = apiKey;
        this.service = service;
    }

    public Single<Resource<Gifs>> getTrendingGifs(int offset) {
        return service.getTrendingGifs(apiKey, offset)
                .subscribeOn(Schedulers.io())
                .map(this::toResource);
    }

    public Single<Resource<Gifs>> getSearchGifs(int offset) {
        return service.getSearchGifs(apiKey, offset)
                .subscribeOn(Schedulers.io())
                .map(this::toResource);
    }

    private <T> Resource<T> toResource(Result<T> result) {
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
