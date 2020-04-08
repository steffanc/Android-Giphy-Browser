package com.giphy.browser;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.viewbinding.BuildConfig;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.giphy.browser.network.Service;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

public class GiphyApp extends Application {

    private Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Fresco.initialize(this);

        RxJavaPlugins.setErrorHandler((throwable -> {
            if (throwable instanceof UndeliverableException) {
                // Stream has been disposed of and we don't care about exceptions thrown after this happens
                Timber.w(throwable, "Ignoring uncaught Rx exception");
            } else {
                throw new RuntimeException(throwable);
            }
        }));

        final Service service = new Retrofit.Builder()
                .baseUrl("https://api.giphy.com")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Service.class);

        repository = new Repository("ixvyd1kLkDNzbSgDGhl2gDQUBM3DECtG", service);
    }

    @NonNull
    public Repository getRepository() {
        return repository;
    }
}
