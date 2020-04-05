package com.giphy.browser.common;

import androidx.annotation.CallSuper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModel<T extends BaseState> extends ViewModel {

    protected final MutableLiveData<T> stateLiveData;
    protected final CompositeDisposable disposables = new CompositeDisposable();

    public BaseViewModel(T state) {
        super();
        stateLiveData = new MutableLiveData<T>(state);
    }

    public LiveData<T> getStateLiveData() {
        return stateLiveData;
    }

    @CallSuper
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    @NotNull
    protected T getState() {
        return Objects.requireNonNull(stateLiveData.getValue());
    }

    protected void setState(T state) {
        stateLiveData.setValue(state);
    }
}
