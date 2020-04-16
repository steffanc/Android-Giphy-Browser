package com.giphy.browser.common

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<T : BaseState>(state: T) : ViewModel() {

    private val _stateLiveData = MutableLiveData<T>(state)
    val stateLiveData: LiveData<T> = _stateLiveData

    val state: T
        get() = _stateLiveData.value!!

    protected val disposables: CompositeDisposable = CompositeDisposable()

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    @MainThread
    protected fun setState(state: T) {
        _stateLiveData.value = state
    }

    protected fun postState(state: T) {
        _stateLiveData.postValue(state)
    }
}
