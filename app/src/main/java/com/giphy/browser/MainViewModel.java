package com.giphy.browser;

import com.giphy.browser.common.BaseViewModel;
import com.giphy.browser.model.Resource;

import org.jetbrains.annotations.NotNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainViewModel extends BaseViewModel<MainState> {

    private final Repository repository;

    public MainViewModel(@NotNull Repository repository) {
        super(new MainState.Builder().build());
        this.repository = repository;
        getTrendingGifs();
    }

    public void searchClicked() {
    }

    public void queryUpdated(String query) {
    }

    public void searchClosed() {
    }

    public void screenRefreshed() {
    }

    public void gifClicked(int position, GifItem item) {
    }

    public void scrollThresholdReached() {
    }

    private void getTrendingGifs() {
        final Disposable disposable = repository.getTrendingGifs()
                .toObservable()
                .startWith(Resource.loading())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((resource) -> {
                    int x = 1;
                });
        disposables.add(disposable);
    }
}
