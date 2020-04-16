package com.giphy.browser.common.network

import com.giphy.browser.common.model.Gifs
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {
    @GET("/v1/gifs/trending")
    fun getTrendingGifs(
        @Query("api_key") apiKey: String?,
        @Query("offset") offset: Int
    ): Single<Result<Gifs>>

    @GET("/v1/gifs/search")
    fun getSearchGifs(
        @Query("api_key") apiKey: String?,
        @Query("q") query: String?,
        @Query("offset") offset: Int
    ): Single<Result<Gifs>>
}
