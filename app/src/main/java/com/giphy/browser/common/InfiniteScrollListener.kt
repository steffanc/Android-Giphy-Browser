package com.giphy.browser.common

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class InfiniteScrollListener(
    private val layoutManager: StaggeredGridLayoutManager,
    private val threshold: Int = 0,
    private val listener: Listener
) : RecyclerView.OnScrollListener() {

    interface Listener {
        fun onScrollThresholdReached()
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val pastVisibleItems = layoutManager.findFirstVisibleItemPositions(null)[0]

            if ((visibleItemCount + pastVisibleItems) >= (totalItemCount - threshold)) {
                listener.onScrollThresholdReached()
            }
        }
    }
}
