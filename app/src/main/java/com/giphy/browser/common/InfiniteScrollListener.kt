package com.giphy.browser.common

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InfiniteScrollListener(
  private val layoutManager: LinearLayoutManager,
  private val listener: Listener,
  private val threshold: Int = 0
) : RecyclerView.OnScrollListener() {

  interface Listener {
    fun onScrollThresholdReached()
  }

  private var pastVisibleItems: Int = 0
  private var visibleItemCount: Int = 0
  private var totalItemCount: Int = 0

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    if (dy > 0) {
      visibleItemCount = layoutManager.childCount
      totalItemCount = layoutManager.itemCount
      pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

      if ((visibleItemCount + pastVisibleItems) >= (totalItemCount - threshold)) {
        listener.onScrollThresholdReached()
      }
    }
  }
}
