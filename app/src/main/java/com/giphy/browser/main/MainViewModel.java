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
import com.giphy.browser.detail.GifDetailActivity;
import com.giphy.browser.model.Gif;
import com.giphy.browser.model.Gifs;
import com.giphy.browser.model.Pagination;
import com.giphy.browser.model.Resource;

import java.util.ArrayList;
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
    private final PublishSubject<FetchGifsEvent> gifsSubject = PublishSubject.create();
    @NonNull
    private Pagination trendingPagination = new Pagination();
    @NonNull
    private Pagination searchPagination = new Pagination();
    @NonNull
    private String lastSuccessfulQuery = "";
    @NonNull
    private Mode mode = Mode.TRENDING;

    MainViewModel(@NonNull Repository repository, @NonNull List<Integer> backgroundColors) {
        super(new MainState.Builder().build());
        this.repository = repository;
        this.backgroundColors = backgroundColors;

        disposables.add(gifsSubject
                .switchMap((event) -> (event.query != null && event.loadingType.equals(LoadingType.LOADING))
                        ? Observable.just(event).delay(500, TimeUnit.MILLISECONDS)
                        : Observable.just(event))
                .switchMap(this::fetchGifs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::reduceGifs));

        gifsSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), null));
    }

    void searchClicked() {
        setState(new MainState.Builder(getState()).setSearchVisible(true).build());
    }

    void queryUpdated(@NonNull String query) {
        mode = Mode.SEARCHING;

        // reset trending state
        trendingPagination = new Pagination();

        if (query.equals(getState().getQuery())) {
            return;
        }

        setState(new MainState.Builder(getState()).setQuery(query).build());

        if (TextUtils.isEmpty(query)) {
            return;
        }

        gifsSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), query));
    }

    void searchClosed() {
        setState(new MainState.Builder(getState())
                .setQuery("")
                .setSearchVisible(false)
                .build());

        // reset searching state
        searchPagination = new Pagination();
        lastSuccessfulQuery = "";

        mode = Mode.TRENDING;
        gifsSubject.onNext(new FetchGifsEvent(LoadingType.LOADING, new Pagination(), null));
    }

    void screenRefreshed() {
        final FetchGifsEvent event = mode.equals(Mode.TRENDING)
                ? new FetchGifsEvent(LoadingType.REFRESHING, new Pagination(), null)
                : new FetchGifsEvent(LoadingType.REFRESHING, new Pagination(), getState().getQuery());
        gifsSubject.onNext(event);
    }

    void gifClicked(int position, @NonNull GifItem item) {
        setState(new MainState.Builder(getState())
                .setNavigateGifDetail(new SingleEvent<>(new GifDetailActivity.Args(
                        item.getWebp(),
                        item.getWidth(),
                        item.getHeight(),
                        item.getBackgroundColor())))
                .build());
    }

    void scrollThresholdReached() {
        if (getState().isLoading() || getState().isRefreshing() || getState().isPaging()) {
            return;
        }

        final FetchGifsEvent event = mode.equals(Mode.TRENDING)
                ? new FetchGifsEvent(LoadingType.PAGING, trendingPagination, null)
                : new FetchGifsEvent(LoadingType.PAGING, searchPagination, lastSuccessfulQuery);
        gifsSubject.onNext(event);
    }

    @NonNull
    private Observable<FetchGifsResult> fetchGifs(@NonNull FetchGifsEvent event) {
        return (event.query != null
                ? repository.getSearchGifs(event.query, event.pagination.nextOffset())
                : repository.getTrendingGifs(event.pagination.nextOffset()))
                .map((resource) -> new FetchGifsResult(event.loadingType, resource, event.query))
                .toObservable()
                .startWith(new FetchGifsResult(event.loadingType, Resource.loading(), event.query));
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
                items.addAll(toItems(result.gifs.getOrThrowData(), backgroundColors));
                items = removeDuplicates(items);

                if (mode.equals(Mode.TRENDING)) {
                    trendingPagination = result.gifs.getOrThrowData().pagination;
                } else {
                    searchPagination = result.gifs.getOrThrowData().pagination;
                    lastSuccessfulQuery = result.query;
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
    private List<GifItem> toItems(@NonNull Gifs gifs, @NonNull List<Integer> backgroundColors) {
        final List<GifItem> items = new ArrayList<>(gifs.data.size());
        for (int i = 0; i < gifs.data.size(); i++) {
            final Gif gif = gifs.data.get(i);
            items.add(new GifItem(gif.id,
                    gif.images.fixedWidth.webp,
                    Integer.parseInt(gif.images.fixedWidth.width),
                    Integer.parseInt(gif.images.fixedWidth.height),
                    backgroundColors.get(i % backgroundColors.size())));
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
