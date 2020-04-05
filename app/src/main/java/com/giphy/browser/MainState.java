package com.giphy.browser;

import com.giphy.browser.common.BaseState;
import com.giphy.browser.common.SingleEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class MainState implements BaseState {
    @NotNull
    private final List<GifItem> items;
    @NotNull
    private final String query;
    private final boolean isLoading;
    private final boolean isRefreshing;
    @Nullable
    private final SingleEvent<String> toast;
    @Nullable
    private final SingleEvent<GifDetailActivity.Args> navigateGifDetail;

    private MainState(@NotNull List<GifItem> items, @NotNull String query, boolean isLoading, boolean isRefreshing, @Nullable SingleEvent<String> toast, @Nullable SingleEvent<GifDetailActivity.Args> navigateGifDetail) {
        this.items = items;
        this.query = query;
        this.isLoading = isLoading;
        this.isRefreshing = isRefreshing;
        this.toast = toast;
        this.navigateGifDetail = navigateGifDetail;
    }

    @NotNull
    public List<GifItem> getItems() {
        return items;
    }

    @NotNull
    public String getQuery() {
        return query;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean isRefreshing() {
        return isRefreshing;
    }

    @Nullable
    public SingleEvent<String> getToast() {
        return toast;
    }

    @Nullable
    public SingleEvent<GifDetailActivity.Args> getNavigateGifDetail() {
        return navigateGifDetail;
    }

    public static class Builder {
        @NotNull
        private List<GifItem> items = Collections.emptyList();
        @NotNull
        private String query = "";
        private boolean isLoading = false;
        private boolean isRefreshing = false;
        @Nullable
        private SingleEvent<String> toast = null;
        @Nullable
        private SingleEvent<GifDetailActivity.Args> navigateGifDetail = null;

        public Builder() {
        }

        public Builder(@NotNull MainState state) {
            this.items = state.items;
            this.query = state.query;
            this.isLoading = state.isLoading;
            this.isRefreshing = state.isRefreshing;
            this.toast = state.toast;
            this.navigateGifDetail = state.navigateGifDetail;
        }

        public void setItems(@NotNull List<GifItem> items) {
            this.items = items;
        }

        public void setQuery(@NotNull String query) {
            this.query = query;
        }

        public void setLoading(boolean loading) {
            isLoading = loading;
        }

        public void setRefreshing(boolean refreshing) {
            isRefreshing = refreshing;
        }

        public void setToast(@Nullable SingleEvent<String> toast) {
            this.toast = toast;
        }

        public void setNavigateGifDetail(@Nullable SingleEvent<GifDetailActivity.Args> navigateGifDetail) {
            this.navigateGifDetail = navigateGifDetail;
        }

        public MainState build() {
            return new MainState(items, query, isLoading, isRefreshing, toast, navigateGifDetail);
        }
    }
}
