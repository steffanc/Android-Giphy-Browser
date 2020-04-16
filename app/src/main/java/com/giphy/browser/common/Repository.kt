package com.giphy.browser.common

import com.giphy.browser.common.model.Gifs
import com.giphy.browser.common.model.Resource
import com.giphy.browser.common.network.ApiException
import com.giphy.browser.common.network.NetworkException
import com.giphy.browser.common.network.Service
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.io.IOException

class Repository(
    private val service: Service,
    private val apiKey: String
) {
    fun getTrendingGifs(offset: Int): Single<Resource<Gifs>> =
        service.getTrendingGifs(apiKey, offset)
            .subscribeOn(Schedulers.io())
            .map { toResource(it) }

    fun getSearchGifs(query: String, offset: Int): Single<Resource<Gifs>> =
        service.getSearchGifs(apiKey, query, offset)
            .subscribeOn(Schedulers.io())
            .map { toResource(it) }

    private fun <T> toResource(result: Result<T>): Resource<T> {
        return if (result.isError) { // Network error
            // Network error
            if (result.error() is IOException) {
                return Resource.Error(NetworkException(result.error()!!))
            }
            // Programmer error
            throw RuntimeException(result.error())
        } else {
            val response: Response<T> = result.response()!!
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                // API error
                Resource.Error(ApiException(response.message()))
            }
        }
    }
}
