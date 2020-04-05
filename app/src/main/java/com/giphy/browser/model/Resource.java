package com.giphy.browser.model;

import androidx.arch.core.util.Function;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Resource<T> {
    enum Status {LOADING, SUCCESS, FAILURE}

    private final Resource.Status status;
    private final T data;
    private final Throwable error;

    public static <T> Resource<T> loading() {
        return new Resource<T>(Status.LOADING, null, null);
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<T>(Resource.Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> failure(Throwable error) {
        return new Resource<T>(Resource.Status.FAILURE, null, error);
    }

    private Resource(Resource.Status status, T data, Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    @NotNull
    public Status getStatus() {
        return status;
    }

    @NotNull
    public T getOrThrowData() {
        return Objects.requireNonNull(data);
    }

    @NotNull
    public Throwable getOrThrowError() {
        return Objects.requireNonNull(error);
    }

    public <R> Resource<R> map(Function<T, R> function) {
        if (status == Status.SUCCESS) {
            return Resource.success(function.apply(data));
        }
        return (Resource<R>) this;
    }
}
