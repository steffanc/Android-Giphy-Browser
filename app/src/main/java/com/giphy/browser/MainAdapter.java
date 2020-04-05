package com.giphy.browser;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.giphy.browser.common.BaseListAdapter;
import com.giphy.browser.common.BaseViewHolder;
import com.giphy.browser.databinding.GifItemViewBinding;

public class MainAdapter extends BaseListAdapter<GifItem> {

    private final GifViewHolder.Listener listener;

    public MainAdapter(GifViewHolder.Listener listener) {
        super(new Callback());
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder<GifItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GifViewHolder(inflateBinding(parent, R.layout.gif_item_view), listener);
    }

    private static class GifViewHolder extends BaseViewHolder<GifItem> {

        interface Listener {
            void onGifClicked(int position, GifItem item);
        }

        private final GifItemViewBinding binding;
        private final Listener listener;

        public GifViewHolder(GifItemViewBinding binding, Listener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        @Override
        public void bind(int position, GifItem item) {
            itemView.setOnClickListener((view) -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onGifClicked(getAdapterPosition(), item);
                }
            });
        }
    }

    private static class Callback extends DiffUtil.ItemCallback<GifItem> {
        @Override
        public boolean areItemsTheSame(@NonNull GifItem oldItem, @NonNull GifItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GifItem oldItem, @NonNull GifItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
