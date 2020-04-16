package com.giphy.browser.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseListAdapter<T>(itemCallback: DiffUtil.ItemCallback<T>) :
  ListAdapter<T, ViewHolder<T>>(itemCallback) {

  fun inflateView(parent: ViewGroup, viewType: Int): View {
    return LayoutInflater.from(parent.context).inflate(viewType, parent, false)
  }

  fun <U : ViewDataBinding> inflateBinding(parent: ViewGroup, layoutId: Int): U {
    return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
  }

  fun isEmpty() = itemCount == 0

  override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
    holder.bind(position, getItem(position))
  }
}
