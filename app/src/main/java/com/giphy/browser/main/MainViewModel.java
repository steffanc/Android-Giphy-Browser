package com.giphy.browser.main;

import android.text.TextUtils;

import com.giphy.browser.Repository;
import com.giphy.browser.common.BaseViewModel;
import com.giphy.browser.common.model.LoadingType;
import com.giphy.browser.model.Gif;
import com.giphy.browser.model.Gifs;
import com.giphy.browser.model.Pagination;
import com.giphy.browser.model.Resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

class MainViewModel extends BaseViewModel<MainState> {

    private final Repository repository;

    private final PublishSubject<FetchGifsEvent> trendingSubject = PublishSubject.create();
    private final PublishSubject<FetchGifsEvent> searchSubject = PublishSubject.create();
    private Pagination trendingPagination = new Pagination();
    private Pagination searchPagination = new Pagination();
    private List<GifItem> cachedTrendingItems = Collections.emptyList();
    private Mode mode = Mode.TRENDING;

    MainViewModel(@NotNull Repository repository) {
        super(new MainState.Builder().build());
        this.repository = repository;

        disposables.add(trendingSubject.switchMap(this::getTrendingGifs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reduceTrendingGifs));

        disposables.add(searchSubject.switchMap((event) -> Observable.just(event).delay(500, TimeUnit.MILLISECONDS))
                .switchMap(this::getSearchGifs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reduceSearchGifs));

        trendingSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), null));
    }

    void searchClicked() {
        mode = Mode.SEARCHING;
    }

    void queryUpdated(@NotNull String query) {
        setState(new MainState.Builder(getState()).setQuery(query).build());
        if (!TextUtils.isEmpty(query)) {
            searchSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), query));
        }
    }

    void searchClosed() {
        mode = Mode.TRENDING;
        setState(new MainState.Builder(getState()).setItems(cachedTrendingItems).build());
    }

    void screenRefreshed() {
        if (mode.equals(Mode.TRENDING)) {
            trendingSubject.onNext(new FetchGifsEvent(LoadingType.REFRESHING, new Pagination(), null));
        } else {
            searchSubject.onNext(new FetchGifsEvent(LoadingType.REFRESHING, new Pagination(), getState().getQuery()));
        }
    }

    void gifClicked(int position, GifItem item) {
    }

    void scrollThresholdReached() {
        if (getState().isLoading() || getState().isRefreshing() || getState().isPaging()) {
            return;
        }

        if (mode.equals(Mode.TRENDING)) {
            trendingSubject.onNext(new FetchGifsEvent(LoadingType.PAGING, trendingPagination, null));
        } else {
            searchSubject.onNext(new FetchGifsEvent(LoadingType.PAGING, searchPagination, getState().getQuery()));
        }
    }

    private Observable<FetchGifsResult> getTrendingGifs(FetchGifsEvent event) {
        return repository.getTrendingGifs(event.pagination.nextOffset())
                .map((resource) -> new FetchGifsResult(event.loadingType, resource, null))
                .toObservable()
                .startWith(new FetchGifsResult(event.loadingType, Resource.loading(), null));
    }

    private Observable<FetchGifsResult> getSearchGifs(FetchGifsEvent event) {
        return repository.getSearchGifs(event.query, event.pagination.nextOffset())
                .map((resource) -> new FetchGifsResult(event.loadingType, resource, null))
                .toObservable()
                .startWith(new FetchGifsResult(event.loadingType, Resource.loading(), null));
    }

    private void reduceTrendingGifs(FetchGifsResult result) {
        switch (result.gifs.getStatus()) {
            case LOADING:
                setState(new MainState.Builder(getState())
                        .setLoading(result.loadingType.equals(LoadingType.LOADING))
                        .setRefreshing(result.loadingType.equals(LoadingType.REFRESHING))
                        .setPaging(result.loadingType.equals(LoadingType.PAGING))
                        .build());
                break;
            case SUCCESS:
                trendingPagination = result.gifs.getOrThrowData().pagination;
                final List<GifItem> items = new ArrayList<>();
                if (result.loadingType.equals(LoadingType.PAGING)) {
                    items.addAll(getState().getItems());
                }
                items.addAll(toItems(result.gifs.getOrThrowData()));
                cachedTrendingItems = items;
                setState(new MainState.Builder(getState())
                        .setItems(items)
                        .setLoading(false)
                        .setRefreshing(false)
                        .setPaging(false)
                        .build());
                break;
            case FAILURE:
                setState(new MainState.Builder(getState())
                        .setLoading(false)
                        .setRefreshing(false)
                        .setPaging(false)
                        .build());
                break;
        }
    }

    private void reduceSearchGifs(FetchGifsResult result) {
        switch (result.gifs.getStatus()) {
            case LOADING:
                setState(new MainState.Builder(getState())
                        .setLoading(result.loadingType.equals(LoadingType.LOADING))
                        .setRefreshing(result.loadingType.equals(LoadingType.REFRESHING))
                        .setPaging(result.loadingType.equals(LoadingType.PAGING))
                        .build());
                break;
            case SUCCESS:
                searchPagination = result.gifs.getOrThrowData().pagination;
                final List<GifItem> items = new ArrayList<>();
                if (result.loadingType.equals(LoadingType.PAGING)) {
                    items.addAll(getState().getItems());
                }
                items.addAll(toItems(result.gifs.getOrThrowData()));
                setState(new MainState.Builder(getState())
                        .setItems(items)
                        .setLoading(false)
                        .setRefreshing(false)
                        .setPaging(false)
                        .build());
                break;
            case FAILURE:
                setState(new MainState.Builder(getState())
                        .setLoading(false)
                        .setRefreshing(false)
                        .setPaging(false)
                        .build());
                break;
        }
    }

    @NotNull
    private List<GifItem> toItems(@NotNull Gifs gifs) {
        final List<GifItem> items = new ArrayList<>(gifs.data.size());
        for (final Gif gif : gifs.data) {
            items.add(new GifItem(gif.id, gif.images.fixedWidth.webp, gif.images.fixedWidth.width, gif.images.fixedWidth.height));
        }
        return items;
    }

    private enum Mode {TRENDING, SEARCHING}

    private static class FetchGifsEvent {
        @NotNull
        final LoadingType loadingType;
        @NotNull
        final Pagination pagination;
        @Nullable
        final String query;

        FetchGifsEvent(@NotNull LoadingType loadingType, @NotNull Pagination pagination, @Nullable String query) {
            this.loadingType = loadingType;
            this.pagination = pagination;
            this.query = query;
        }
    }

    private static class FetchGifsResult {
        @NotNull
        final LoadingType loadingType;
        @NotNull
        final Resource<Gifs> gifs;
        @Nullable
        final String query;

        FetchGifsResult(@NotNull LoadingType loadingType, @NotNull Resource<Gifs> gifs, @Nullable String query) {
            this.loadingType = loadingType;
            this.gifs = gifs;
            this.query = query;
        }
    }
}
