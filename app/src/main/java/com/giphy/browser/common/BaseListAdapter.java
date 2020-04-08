package com.giphy.browser.common;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public abstract class BaseListAdapter<T extends BaseItem> extends ListAdapter<T, BaseViewHolder<T>> {
    public BaseListAdapter(@NonNull DiffUtil.ItemCallback<T> callback) {
        super(callback);
    }

    protected <U extends ViewDataBinding> U inflateBinding(ViewGroup parent, int layoutId) {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), layoutId, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<T> holder, int position) {
        holder.bind(position, getItem(position));
    }
}
