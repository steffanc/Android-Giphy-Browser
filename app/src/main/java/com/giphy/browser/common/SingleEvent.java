package com.giphy.browser.common;

import androidx.arch.core.util.Function;

public class SingleEvent<T> {
    private final T data;
    private boolean consumed = false;

    public SingleEvent(T data) {
        this.data = data;
    }

    public void maybeConsume(Function<T, Void> function) {
        if (!consumed) {
            consumed = true;
            function.apply(data);
        }
    }
}
