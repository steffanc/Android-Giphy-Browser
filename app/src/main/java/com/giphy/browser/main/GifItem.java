package com.giphy.browser.main;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.giphy.browser.common.BaseItem;

public class GifItem implements BaseItem {
    @NonNull
    private final String id;
    @NonNull
    private final String webp;
    private final int width;
    private final int height;
    @ColorRes
    private final int backgroundColor;

    public GifItem(@NonNull String id, @NonNull String webp, @NonNull int width, @NonNull int height, int backgroundColor) {
        this.id = id;
        this.webp = webp;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @NonNull
    public String getWebp() {
        return webp;
    }

    @NonNull
    public int getWidth() {
        return width;
    }

    @NonNull
    public int getHeight() {
        return height;
    }

    @ColorRes
    public int getBackgroundColor() {
        return backgroundColor;
    }
}
