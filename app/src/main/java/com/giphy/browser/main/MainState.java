package com.giphy.browser.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.browser.common.BaseState;
import com.giphy.browser.common.model.SingleEvent;
import com.giphy.browser.detail.GifDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainState implements BaseState {
    @NonNull
    private final List<GifItem> items;
    @Nullable
    private final String query;
    private final boolean isSearchVisible;
    private final boolean isLoading;
    private final boolean isRefreshing;
    private final boolean isPaging;
    @Nullable
    private final SingleEvent<Integer> toast;
    @Nullable
    private final SingleEvent<GifDetailActivity.Args> navigateGifDetail;

    private MainState(@NonNull List<GifItem> items, @Nullable String query, boolean isSearchVisible, boolean isLoading, boolean isRefreshing, boolean isPaging, @Nullable SingleEvent<Integer> toast, @Nullable SingleEvent<GifDetailActivity.Args> navigateGifDetail) {
        this.items = items;
        this.query = query;
        this.isSearchVisible = isSearchVisible;
        this.isLoading = isLoading;
        this.isRefreshing = isRefreshing;
        this.isPaging = isPaging;
        this.toast = toast;
        this.navigateGifDetail = navigateGifDetail;
    }

    @NonNull
    public List<GifItem> getItems() {
        return new ArrayList<>(items);
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    public boolean isSearchVisible() {
        return isSearchVisible;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    public boolean isPaging() {
        return isPaging;
    }

    @Nullable
    public SingleEvent<Integer> getToast() {
        return toast;
    }

    @Nullable
    public SingleEvent<GifDetailActivity.Args> getNavigateGifDetail() {
        return navigateGifDetail;
    }

    public static class Builder {
        @NonNull
        private List<GifItem> items = Collections.emptyList();
        @Nullable
        private String query = null;
        private boolean isSearchVisible = false;
        private boolean isLoading = false;
        private boolean isRefreshing = false;
        private boolean isPaging = false;
        @Nullable
        private SingleEvent<Integer> toast = null;
        @Nullable
        private SingleEvent<GifDetailActivity.Args> navigateGifDetail = null;

        public Builder() {
        }

        public Builder(@NonNull MainState state) {
            this.items = state.items;
            this.query = state.query;
            this.isSearchVisible = state.isSearchVisible;
            this.isLoading = state.isLoading;
            this.isRefreshing = state.isRefreshing;
            this.isPaging = state.isPaging;
            this.toast = state.toast;
            this.navigateGifDetail = state.navigateGifDetail;
        }

        @NonNull
        public Builder setItems(@NonNull List<GifItem> items) {
            this.items = items;
            return this;
        }

        @Nullable
        public Builder setQuery(@Nullable String query) {
            this.query = query;
            return this;
        }

        @NonNull
        public Builder setSearchVisible(boolean searchVisible) {
            this.isSearchVisible = searchVisible;
            return this;
        }

        @NonNull
        public Builder setLoading(boolean loading) {
            isLoading = loading;
            return this;
        }

        @NonNull
        public Builder setRefreshing(boolean refreshing) {
            isRefreshing = refreshing;
            return this;
        }

        @NonNull
        public Builder setPaging(boolean paging) {
            isPaging = paging;
            return this;
        }

        @NonNull
        public Builder setToast(@Nullable SingleEvent<Integer> toast) {
            this.toast = toast;
            return this;
        }

        @NonNull
        public Builder setNavigateGifDetail(@Nullable SingleEvent<GifDetailActivity.Args> navigateGifDetail) {
            this.navigateGifDetail = navigateGifDetail;
            return this;
        }

        @NonNull
        public MainState build() {
            return new MainState(items, query, isSearchVisible, isLoading, isRefreshing, isPaging, toast, navigateGifDetail);
        }
    }
}
