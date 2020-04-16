package com.giphy.browser

import android.app.Application
import androidx.viewbinding.BuildConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.giphy.browser.common.Repository
import com.giphy.browser.common.network.Service
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import timber.log.Timber.DebugTree

class GiphyApp : Application() {
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        Fresco.initialize(this)

        RxJavaPlugins.setErrorHandler { throwable: Throwable ->
            if (throwable is UndeliverableException) { // Stream has been disposed of and we don't care about exceptions thrown after this happens
                Timber.w(throwable, "Ignoring uncaught Rx exception")
            } else {
                throw throwable
            }
        }
        val service = Retrofit.Builder()
            .baseUrl("https://api.giphy.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(Service::class.java)
        repository = Repository(service, "ixvyd1kLkDNzbSgDGhl2gDQUBM3DECtG")
    }

}
