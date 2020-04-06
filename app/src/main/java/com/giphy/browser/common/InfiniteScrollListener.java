package com.giphy.browser.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    public interface Listener {
        void onScrollThresholdReached();
    }

    @NotNull
    private final LinearLayoutManager layoutManager;
    private final int threshold;
    @NotNull
    private final Listener listener;

    private int pastVisibleItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;

    public InfiniteScrollListener(@NotNull LinearLayoutManager layoutManager, int threshold, @NotNull Listener listener) {
        this.layoutManager = layoutManager;
        this.threshold = threshold;
        this.listener = listener;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > 0) {
            visibleItemCount = layoutManager.getChildCount();
            totalItemCount = layoutManager.getItemCount();
            pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

            if ((visibleItemCount + pastVisibleItems) >= (totalItemCount - threshold)) {
                listener.onScrollThresholdReached();
            }
        }
    }
}
