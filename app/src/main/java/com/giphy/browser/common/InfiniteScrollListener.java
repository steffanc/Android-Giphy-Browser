package com.giphy.browser.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    public interface Listener {
        void onScrollThresholdReached();
    }

    @NonNull
    private final StaggeredGridLayoutManager layoutManager;
    private final int threshold;
    @NonNull
    private final Listener listener;

    private int pastVisibleItems = 0;
    private int visibleItemCount = 0;
    private int totalItemCount = 0;

    public InfiniteScrollListener(@NonNull StaggeredGridLayoutManager layoutManager, int threshold, @NonNull Listener listener) {
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
            pastVisibleItems = layoutManager.findFirstVisibleItemPositions(null)[0];

            if ((visibleItemCount + pastVisibleItems) >= (totalItemCount - threshold)) {
                listener.onScrollThresholdReached();
            }
        }
    }
}
