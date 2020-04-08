package com.giphy.browser.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.giphy.browser.R;
import com.giphy.browser.common.BaseActivity;
import com.giphy.browser.databinding.ActivityGifDetailBinding;

import java.io.Serializable;
import java.util.Objects;

public class GifDetailActivity extends BaseActivity {

    private ActivityGifDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gif_detail);

        final Args args = Objects.requireNonNull((Args) getIntent().getSerializableExtra(ARGS));

        setTitle(R.string.details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding.gif.setAspectRatio((float) args.width / args.height);
        binding.gif.getHierarchy().setPlaceholderImage(args.backgroundColor);
        binding.gif.setController(
                Fresco.newDraweeControllerBuilder()
                        .setAutoPlayAnimations(true)
                        .setOldController(binding.gif.getController())
                        .setUri(args.webp)
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

    private static final String ARGS = "args";

    public static Intent newIntent(@NonNull Context context, @NonNull Args args) {
        return new Intent(context, GifDetailActivity.class)
                .putExtra(ARGS, args);
    }

    public static class Args implements Serializable {
        @NonNull
        public final String webp;
        public final int width;
        public final int height;
        @ColorRes
        public final int backgroundColor;

        public Args(@NonNull String webp, @NonNull int width, @NonNull int height, int backgroundColor) {
            this.webp = webp;
            this.width = width;
            this.height = height;
            this.backgroundColor = backgroundColor;
        }
    }
}
