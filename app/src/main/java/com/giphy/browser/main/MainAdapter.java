package com.giphy.browser.main;

import android.graphics.drawable.Animatable;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.giphy.browser.R;
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
            binding.getRoot().setOnClickListener((view) -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onGifClicked(getAdapterPosition(), item);
                }
            });

            final ControllerListener listener = new BaseControllerListener<ImageInfo>() {
                @Override
                public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                    updateViewSize(imageInfo);
                }

                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    updateViewSize(imageInfo);
                }

                private void updateViewSize(@Nullable ImageInfo imageInfo) {
                    if (imageInfo != null) {
                        binding.gif.setAspectRatio((float) imageInfo.getWidth() / imageInfo.getHeight());
                    }
                }
            };

            binding.gif.setController(
                    Fresco.newDraweeControllerBuilder()
                            .setAutoPlayAnimations(true)
                            .setControllerListener(listener)
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
