package com.giphy.browser.common;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModel<T extends BaseState> extends ViewModel {

    @NonNull
    private final MutableLiveData<T> stateLiveData;
    @NonNull
    protected final CompositeDisposable disposables = new CompositeDisposable();

    public BaseViewModel(@NonNull T state) {
        super();
        stateLiveData = new MutableLiveData<T>(state);
    }

    @CallSuper
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    @NonNull
    public LiveData<T> getStateLiveData() {
        return stateLiveData;
    }

    @NonNull
    public T getState() {
        return Objects.requireNonNull(stateLiveData.getValue());
    }

    @MainThread
    protected void setState(@NonNull T state) {
        stateLiveData.setValue(state);
    }
}
