package com.giphy.browser.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.giphy.browser.R;
import com.giphy.browser.common.BaseActivity;
import com.giphy.browser.databinding.ActivityGifDetailBinding;

import java.util.Objects;

public class GifDetailActivity extends BaseActivity {

    private ActivityGifDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gif_detail);

        final String webpUri = Objects.requireNonNull(getIntent().getStringExtra(WEBP_URI));

        setTitle(R.string.details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                        .setUri(webpUri)
                        .build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final String WEBP_URI = "webp_uri";

    public static Intent newIntent(@NonNull Context context, @NonNull String webpUri) {
        return new Intent(context, GifDetailActivity.class)
                .putExtra(WEBP_URI, webpUri);
    }
}
