package com.giphy.browser.gif_list

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.giphy.browser.GiphyApp
import com.giphy.browser.R
import com.giphy.browser.common.BaseActivity
import com.giphy.browser.common.InfiniteScrollListener
import com.giphy.browser.databinding.ActivityGifListBinding
import com.giphy.browser.gif_detail.GifDetailActivity

class GifListActivity : BaseActivity() {
    private lateinit var binding: ActivityGifListBinding
    private lateinit var viewModel: GifListViewModel
    private lateinit var searchView: SearchView
    private lateinit var adapter: GifListAdapter
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gif_list)

        val array = resources.obtainTypedArray(R.array.backgroundColors)
        val backgroundColors = mutableListOf<Int>()
        for (i in 0 until array.length()) {
            backgroundColors.add(array.getResourceId(i, 0))
        }
        array.recycle()

        val repository = (application as GiphyApp).repository
        viewModel = createViewModel { GifListViewModel(repository, backgroundColors) }
        viewModel.stateLiveData
            .observe(this, Observer { render(it) })

        adapter = GifListAdapter(object : GifListAdapter.GifViewHolder.Listener {
            override fun onGifClicked(position: Int, item: GifItem) {
                viewModel.gifClicked(position, item)
            }
        })
        binding.content.adapter = adapter
        val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        binding.content.layoutManager = layoutManager
        binding.content.addOnScrollListener(
            InfiniteScrollListener(layoutManager, 10, object : InfiniteScrollListener.Listener {
                override fun onScrollThresholdReached() {
                    viewModel.scrollThresholdReached()
                }
            })
        )

        binding.swipeContainer.setOnRefreshListener { viewModel.screenRefreshed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gif_list_menu, menu)
        searchView = menu!!.findItem(R.id.search).actionView as SearchView

        searchView.setQuery(viewModel.state.query, false)
        searchView.isIconified = !viewModel.state.isSearchVisible

        // Don't expand search view to full screen in landscape mode
        searchView.imeOptions = searchView.imeOptions or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.setOnSearchClickListener { view: View? -> viewModel.searchClicked() }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String) = true
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.queryUpdated(newText)
                return true
            }
        })
        searchView.setOnCloseListener {
            viewModel.searchClosed()
            false
        }
        return true
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.isIconified = true
        } else {
            super.onBackPressed()
        }
    }

    private fun render(state: GifListViewModel.State) {
        adapter.submitList(state.items)

        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.swipeContainer.isRefreshing = state.isRefreshing
        binding.swipeContainer.isEnabled = !state.isLoading

        state.toast?.maybeConsume {
            // Prevent multiple toasts from stacking
            toast?.cancel()
            toast = Toast.makeText(this, getString(it), Toast.LENGTH_SHORT)
            toast!!.show()
        }

        state.navigateGifDetail?.maybeConsume {
            startActivity(GifDetailActivity.newIntent(this, it))
        }
    }
}
