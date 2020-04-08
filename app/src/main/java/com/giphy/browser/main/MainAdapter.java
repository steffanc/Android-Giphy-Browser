package com.giphy.browser.main;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.giphy.browser.R;
import com.giphy.browser.common.BaseListAdapter;
import com.giphy.browser.common.BaseViewHolder;
import com.giphy.browser.databinding.GifItemViewBinding;

public class MainAdapter extends BaseListAdapter<GifItem> {

    @NonNull
    private final GifViewHolder.Listener listener;

    public MainAdapter(@NonNull GifViewHolder.Listener listener) {
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

        @NonNull
        private final GifItemViewBinding binding;
        @NonNull
        private final Listener listener;

        public GifViewHolder(@NonNull GifItemViewBinding binding, @NonNull Listener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        @Override
        public void bind(int position, GifItem item) {
            binding.getRoot().setOnClickListener((view) -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onGifClicked(getAdapterPosition(), item);
                }
            });

            binding.gif.setAspectRatio((float) item.getWidth() / item.getHeight());
            binding.gif.getHierarchy().setPlaceholderImage(item.getBackgroundColor());
            binding.gif.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setAutoPlayAnimations(true)
                            .setOldController(binding.gif.getController())
                            .setUri(item.getWebp())
                            .build());
        }
    }

    private static class Callback extends DiffUtil.ItemCallback<GifItem> {
        @Override
        public boolean areItemsTheSame(@NonNull GifItem oldItem, @NonNull GifItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull GifItem oldItem, @NonNull GifItem newItem) {
            return true;
        }
    }
}
