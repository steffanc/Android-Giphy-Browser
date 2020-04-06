package com.giphy.browser.common.model;

import com.giphy.browser.common.util.Block;

public class SingleEvent<T> {
    private final T data;
    private boolean consumed = false;

    public SingleEvent(T data) {
        this.data = data;
    }

    public void maybeConsume(Block<T> block) {
        if (!consumed) {
            consumed = true;
            block.invoke(data);
        }
    }
}
