package com.giphy.browser.common

import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.answers.reddit.app.base.model.BaseState
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

  @VisibleForTesting(otherwise = VisibleForTesting.NONE)
  fun setStateForTesting(state: T) {
    setState(state)
  }
}
