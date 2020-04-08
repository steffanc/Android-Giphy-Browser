package com.giphy.browser.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class Resource<T> {
    public enum Status {LOADING, SUCCESS, FAILURE}

    @NonNull
    private final Resource.Status status;
    @Nullable
    private final T data;
    @Nullable
    private final Throwable error;

    @NonNull
    public static <T> Resource<T> loading() {
        return new Resource<T>(Status.LOADING, null, null);
    }

    @NonNull
    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<T>(Resource.Status.SUCCESS, data, null);
    }

    @NonNull
    public static <T> Resource<T> failure(@NonNull Throwable error) {
        return new Resource<T>(Resource.Status.FAILURE, null, error);
    }

    private Resource(@NonNull Resource.Status status, @Nullable T data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @NonNull
    public T getOrThrowData() {
        return Objects.requireNonNull(data);
    }

    @NonNull
    public Throwable getOrThrowError() {
        return Objects.requireNonNull(error);
    }
}
