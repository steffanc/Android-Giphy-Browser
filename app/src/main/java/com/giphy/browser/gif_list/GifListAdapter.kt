package com.giphy.browser.gif_list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.giphy.browser.R
import com.giphy.browser.common.BaseListAdapter
import com.giphy.browser.common.BaseViewHolder
import com.giphy.browser.databinding.GifItemViewBinding

class GifListAdapter(private val listener: GifViewHolder.Listener) :
    BaseListAdapter<GifItem>(Callback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<GifItem> {
        return GifViewHolder(inflateBinding(parent, R.layout.gif_item_view), listener)
    }

    class GifViewHolder(
        private val binding: GifItemViewBinding,
        private val listener: Listener
    ) : BaseViewHolder<GifItem>(binding.root) {

        interface Listener {
            fun onGifClicked(position: Int, item: GifItem)
        }

        override fun bind(position: Int, item: GifItem) {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onGifClicked(adapterPosition, item)
                }
            }

            binding.gif.aspectRatio = item.aspectRatio
            binding.gif.hierarchy.setPlaceholderImage(item.backgroundColor)
            binding.gif.controller = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                .setOldController(binding.gif.controller)
                .setUri(item.webp)
                .build()
        }
    }

    private class Callback : DiffUtil.ItemCallback<GifItem>() {
        override fun areItemsTheSame(oldItem: GifItem, newItem: GifItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GifItem, newItem: GifItem) = true
    }
}
