package com.giphy.browser;

import com.giphy.browser.common.BaseViewModel;
import com.giphy.browser.model.Gifs;
import com.giphy.browser.model.Pagination;
import com.giphy.browser.model.Resource;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

public class MainViewModel extends BaseViewModel<MainState> {

    private final Repository repository;
    private final PublishSubject<Object> trendingSubject = PublishSubject.create();

    private Pagination trendingPagination = new Pagination();
    private Pagination searchPagination = new Pagination();

    public MainViewModel(@NotNull Repository repository) {
        super(new MainState.Builder().build());
        this.repository = repository;
        disposables.add(trendingSubject.switchMap((object) -> getTrendingGifs())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reduceTrendingGifs));
        trendingSubject.onNext(new Object());
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

    private Observable<Resource<Gifs>> getTrendingGifs() {
        return repository.getTrendingGifs(trendingPagination.offset)
                .toObservable()
                .startWith(Resource.loading());
    }

    private void reduceTrendingGifs(Resource<Gifs> resource) {
        switch (resource.getStatus()) {
            case LOADING:
                setState(new MainState.Builder(getState()).setLoading(true).build());
                break;
            case SUCCESS:
                setState(new MainState.Builder(getState()).setLoading(false).build());
                break;
            case FAILURE:
                setState(new MainState.Builder(getState()).setLoading(false).build());
                break;
        }
    }
}
