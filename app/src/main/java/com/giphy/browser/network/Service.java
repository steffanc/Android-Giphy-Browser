package com.giphy.browser.network;

import io.reactivex.Single;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("/v1/gifs/trending")
    Single<Result<GifsResponse>> getTrendingGifs(@Query("api_key") String apiKey);

    @GET("/v1/gifs/search")
    Single<Result<GifsResponse>> getSearchGifs(@Query("api_key") String apiKey);
}
