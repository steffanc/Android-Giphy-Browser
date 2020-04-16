package com.giphy.browser.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<T : BaseItem>(itemCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, BaseViewHolder<T>>(itemCallback) {

    fun <U : ViewDataBinding> inflateBinding(parent: ViewGroup, layoutId: Int): U {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
    }

    fun isEmpty() = itemCount == 0

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(position, getItem(position))
    }
}
