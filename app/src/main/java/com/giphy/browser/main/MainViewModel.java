package com.giphy.browser.main;

import android.text.TextUtils;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.browser.R;
import com.giphy.browser.Repository;
import com.giphy.browser.common.BaseViewModel;
import com.giphy.browser.common.model.LoadingType;
import com.giphy.browser.common.model.SingleEvent;
import com.giphy.browser.model.Gif;
import com.giphy.browser.model.Gifs;
import com.giphy.browser.model.Pagination;
import com.giphy.browser.model.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

class MainViewModel extends BaseViewModel<MainState> {

    @NonNull
    private final Repository repository;
    @NonNull
    private final List<Integer> backgroundColors;
    @NonNull
    private final PublishSubject<FetchGifsEvent> trendingSubject = PublishSubject.create();
    @NonNull
    private final PublishSubject<FetchGifsEvent> searchSubject = PublishSubject.create();
    @NonNull
    private Pagination trendingPagination = new Pagination();
    @NonNull
    private Pagination searchPagination = new Pagination();
    @NonNull
    private List<GifItem> cachedTrendingItems = Collections.emptyList();
    @NonNull
    private Mode mode = Mode.TRENDING;

    MainViewModel(@NonNull Repository repository, @NonNull List<Integer> backgroundColors) {
        super(new MainState.Builder().build());
        this.repository = repository;
        this.backgroundColors = backgroundColors;

        setMode(Mode.TRENDING);
        trendingSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), null));
    }

    void searchClicked() {
        setMode(Mode.SEARCHING);
    }

    void queryUpdated(@NonNull String query) {
        if (query.equals(getState().getQuery())) {
            return;
        }

        setState(new MainState.Builder(getState()).setQuery(query).build());

        if (TextUtils.isEmpty(query)) {
            return;
        }

        searchSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), query));
    }

    void searchClosed() {
        setMode(Mode.TRENDING);
    }

    void screenRefreshed() {
        if (mode.equals(Mode.TRENDING)) {
            trendingSubject.onNext(new FetchGifsEvent(LoadingType.REFRESHING, new Pagination(), null));
        } else {
            searchSubject.onNext(new FetchGifsEvent(LoadingType.REFRESHING, new Pagination(), getState().getQuery()));
        }
    }

    void gifClicked(int position, @NonNull GifItem item) {
        setState(new MainState.Builder(getState()).setNavigateGifDetail(new SingleEvent<>(item.getWebp())).build());
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

    private void setMode(@NonNull Mode mode) {
        this.mode = mode;
        disposables.clear();

        if (mode.equals(Mode.TRENDING)) {
            disposables.add(trendingSubject
                    .switchMap(this::fetchGifs)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::reduceGifs));
        } else {
            searchPagination = new Pagination();
            disposables.add(searchSubject
                    .switchMap((event) -> (event.query != null && event.loadingType.equals(LoadingType.LOADING))
                            ? Observable.just(event).delay(500, TimeUnit.MILLISECONDS)
                            : Observable.just(event))
                    .switchMap(this::fetchGifs)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::reduceGifs));
        }

        setState(new MainState.Builder(getState())
                .setItems(cachedTrendingItems)
                .setQuery("")
                .setSearchVisible(mode.equals(Mode.SEARCHING))
                .setLoading(false)
                .setRefreshing(false)
                .setPaging(false)
                .build());
    }

    @NonNull
    private Observable<FetchGifsResult> fetchGifs(@NonNull FetchGifsEvent event) {
        return (event.query != null
                ? repository.getSearchGifs(event.query, event.pagination.nextOffset())
                : repository.getTrendingGifs(event.pagination.nextOffset()))
                .map((resource) -> new FetchGifsResult(event.loadingType, resource, null))
                .toObservable()
                .startWith(new FetchGifsResult(event.loadingType, Resource.loading(), null));
    }

    @MainThread
    private void reduceGifs(@NonNull FetchGifsResult result) {
        switch (result.gifs.getStatus()) {
            case LOADING:
                setState(new MainState.Builder(getState())
                        .setLoading(result.loadingType.equals(LoadingType.LOADING))
                        .setRefreshing(result.loadingType.equals(LoadingType.REFRESHING))
                        .setPaging(result.loadingType.equals(LoadingType.PAGING))
                        .build());
                break;
            case SUCCESS:
                List<GifItem> items = new ArrayList<>();
                if (result.loadingType.equals(LoadingType.PAGING)) {
                    items.addAll(getState().getItems());
                }
                items.addAll(toItems(result.gifs.getOrThrowData()));
                items = removeDuplicates(items);

                if (mode.equals(Mode.TRENDING)) {
                    trendingPagination = result.gifs.getOrThrowData().pagination;
                    cachedTrendingItems = items;
                } else {
                    searchPagination = result.gifs.getOrThrowData().pagination;
                }

                setState(new MainState.Builder(getState())
                        .setItems(items)
                        .setLoading(false)
                        .setRefreshing(false)
                        .setPaging(false)
                        .build());
                break;
            case FAILURE:
                setState(new MainState.Builder(getState())
                        .setToast(new SingleEvent<>(R.string.failed_fetch))
                        .setLoading(false)
                        .setRefreshing(false)
                        .setPaging(false)
                        .build());
                break;
        }
    }

    @NonNull
    private List<GifItem> toItems(@NonNull Gifs gifs) {
        final List<GifItem> items = new ArrayList<>(gifs.data.size());
        for (int i = 0; i < gifs.data.size(); i++) {
            final Gif gif = gifs.data.get(i);
            items.add(new GifItem(gif.id, gif.images.fixedWidth.webp, gif.images.fixedWidth.width,
                    gif.images.fixedWidth.height, backgroundColors.get(i % backgroundColors.size())));
        }
        return items;
    }

    @NonNull
    private List<GifItem> removeDuplicates(@NonNull List<GifItem> gifs) {
        final List<GifItem> filteredItems = new ArrayList<>(gifs.size());
        final Set<String> idSet = new HashSet<>(gifs.size());
        for (GifItem item : gifs) {
            if (!idSet.contains(item.getId())) {
                filteredItems.add(item);
                idSet.add(item.getId());
            }
        }
        return filteredItems;
    }

    private enum Mode {TRENDING, SEARCHING}

    private static class FetchGifsEvent {
        @NonNull
        final LoadingType loadingType;
        @NonNull
        final Pagination pagination;
        @Nullable
        final String query;

        FetchGifsEvent(@NonNull LoadingType loadingType, @NonNull Pagination pagination, @Nullable String query) {
            this.loadingType = loadingType;
            this.pagination = pagination;
            this.query = query;
        }
    }

    private static class FetchGifsResult {
        @NonNull
        final LoadingType loadingType;
        @NonNull
        final Resource<Gifs> gifs;
        @Nullable
        final String query;

        FetchGifsResult(@NonNull LoadingType loadingType, @NonNull Resource<Gifs> gifs, @Nullable String query) {
            this.loadingType = loadingType;
            this.gifs = gifs;
            this.query = query;
        }
    }
}
