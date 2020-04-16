package com.giphy.browser.gif_list

import androidx.annotation.MainThread
import com.giphy.browser.R
import com.giphy.browser.common.BaseState
import com.giphy.browser.common.BaseViewModel
import com.giphy.browser.common.Repository
import com.giphy.browser.common.model.*
import com.giphy.browser.gif_detail.GifDetailActivity.Args
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class GifListViewModel(
    private val repository: Repository,
    private val backgroundColors: List<Int>
) : BaseViewModel<GifListViewModel.State>(State()) {

    private val gifsSubject: PublishSubject<FetchGifsEvent> = PublishSubject.create()
    private var lastSuccessfulResult: FetchGifsResult? = null

    data class State(
        val items: List<GifItem> = emptyList(),
        val query: String? = null,
        val isSearchVisible: Boolean = false,
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val isPaging: Boolean = false,
        val toast: SingleEvent<Int>? = null,
        val navigateGifDetail: SingleEvent<Args>? = null
    ) : BaseState

    init {
        gifsSubject
            .switchMap {
                if (it.query != null && it.loadingType == LoadingType.LOADING)
                    Observable.just(it).delay(500, TimeUnit.MILLISECONDS)
                else Observable.just(it)
            }.switchMap { fetchGifs(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { reduceGifs(it) }
            .let { disposables.add(it) }

        gifsSubject.onNext(FetchGifsEvent(LoadingType.LOADING, Pagination(), null))
    }

    fun searchClicked() {
        setState(state.copy(isSearchVisible = true))
    }

    fun queryUpdated(query: String) {
        if (query == state.query) {
            return
        }

        setState(state.copy(query = query))

        if (query.isEmpty()) {
            return
        }

        gifsSubject.onNext(FetchGifsEvent(LoadingType.LOADING, Pagination(), query))
    }

    fun searchClosed() {
        val query = state.query
        setState(state.copy(query = null, isSearchVisible = false))

        if (query != null) {
            gifsSubject.onNext(FetchGifsEvent(LoadingType.LOADING, Pagination(), null))
        }
    }

    fun screenRefreshed() {
        gifsSubject.onNext(FetchGifsEvent(LoadingType.REFRESHING, Pagination(), state.query))
    }

    fun gifClicked(position: Int, item: GifItem) {
        setState(
            state.copy(
                navigateGifDetail = SingleEvent(
                    Args(item.webp, item.aspectRatio, item.backgroundColor)
                )
            )
        )
    }

    fun scrollThresholdReached() {
        if (state.isLoading || state.isRefreshing || state.isPaging) {
            return
        }

        gifsSubject.onNext(
            FetchGifsEvent(
                LoadingType.PAGING,
                (lastSuccessfulResult!!.gifs as Resource.Success).data.pagination,
                lastSuccessfulResult!!.query
            )
        )
    }

    private fun fetchGifs(event: FetchGifsEvent): Observable<FetchGifsResult> {
        return if (event.query != null) {
            repository.getSearchGifs(event.query, event.pagination.nextOffset())
        } else {
            repository.getTrendingGifs(event.pagination.nextOffset())
        }
            .map { FetchGifsResult(event.loadingType, it, event.query) }
            .toObservable()
            .startWith(FetchGifsResult(event.loadingType, Resource.Loading, event.query))
    }

    @MainThread
    private fun reduceGifs(result: FetchGifsResult) {
        when (result.gifs) {
            is Resource.Loading -> {
                setState(
                    state.copy(
                        isLoading = result.loadingType == LoadingType.LOADING,
                        isRefreshing = result.loadingType == LoadingType.REFRESHING,
                        isPaging = result.loadingType == LoadingType.PAGING
                    )
                )
            }
            is Resource.Success -> {
                lastSuccessfulResult = result
                val items = if (state.isPaging) {
                    state.items
                } else {
                    emptyList()
                } + result.gifs.data.data.mapIndexed { i, gif ->
                    GifItem(
                        gif.id,
                        gif.images.fixedWidth.webp,
                        Integer.parseInt(gif.images.fixedWidth.width),
                        Integer.parseInt(gif.images.fixedWidth.height),
                        backgroundColors[i % backgroundColors.size]
                    )
                }.distinctBy { item -> item.id }

                setState(
                    state.copy(
                        items = items,
                        isLoading = false,
                        isRefreshing = false,
                        isPaging = false
                    )
                )
            }
            is Resource.Error -> {
                setState(
                    state.copy(
                        toast = SingleEvent(R.string.failed_fetch),
                        isLoading = false,
                        isRefreshing = false,
                        isPaging = false
                    )
                )
            }
        }
    }

    private data class FetchGifsEvent(
        val loadingType: LoadingType,
        val pagination: Pagination,
        val query: String?
    )

    private data class FetchGifsResult(
        val loadingType: LoadingType,
        val gifs: Resource<Gifs>,
        val query: String?
    )
}
