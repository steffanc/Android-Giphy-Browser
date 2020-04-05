package com.giphy.browser;

import android.app.Application;

import androidx.viewbinding.BuildConfig;

import com.giphy.browser.network.Service;

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

        final Service service = new Retrofit.Builder()
                .baseUrl("https://api.giphy.com")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(Service.class);

        repository = new Repository("ixvyd1kLkDNzbSgDGhl2gDQUBM3DECtG", service);
    }

    public Repository getRepository() {
        return repository;
    }
}
