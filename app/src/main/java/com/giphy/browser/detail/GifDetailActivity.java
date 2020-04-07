package com.giphy.browser.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.giphy.browser.R;
import com.giphy.browser.common.BaseActivity;
import com.giphy.browser.databinding.ActivityGifDetailBinding;

import java.io.Serializable;

public class GifDetailActivity extends BaseActivity {

    private ActivityGifDetailBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gif_detail);

        final Args args = (Args) getIntent().getSerializableExtra(ARGS);
    }

    public static class Args implements Serializable {
    }

    private static final String ARGS = "args";

    public static Intent newIntent(Context context, Args args) {
        return new Intent(context, GifDetailActivity.class)
                .putExtra(ARGS, args);
    }
}
