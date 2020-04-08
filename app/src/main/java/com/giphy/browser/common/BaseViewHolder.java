package com.giphy.browser.common;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T extends BaseItem> extends RecyclerView.ViewHolder {
    protected BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(int position, T item);
}
