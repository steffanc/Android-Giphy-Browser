package com.giphy.browser.main;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;

import com.giphy.browser.common.BaseItem;

public class GifItem implements BaseItem {
    @NonNull
    private final String id;
    @NonNull
    private final String webp;
    @NonNull
    private final String width;
    @NonNull
    private final String height;
    @ColorRes
    private final int backgroundColor;

    public GifItem(@NonNull String id, @NonNull String webp, @NonNull String width, @NonNull String height, int backgroundColor) {
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
    public String getWidth() {
        return width;
    }

    @NonNull
    public String getHeight() {
        return height;
    }

    @ColorRes
    public int getBackgroundColor() {
        return backgroundColor;
    }
}
