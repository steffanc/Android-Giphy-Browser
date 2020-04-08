package com.giphy.browser.common.network;

import com.giphy.browser.common.model.Gifs;

import io.reactivex.Single;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("/v1/gifs/trending")
    Single<Result<Gifs>> getTrendingGifs(@Query("api_key") String apiKey, @Query("offset") int offset);

    @GET("/v1/gifs/search")
    Single<Result<Gifs>> getSearchGifs(@Query("api_key") String apiKey, @Query("q") String query, @Query("offset") int offset);
}
