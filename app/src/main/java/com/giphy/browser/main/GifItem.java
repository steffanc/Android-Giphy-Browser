package com.giphy.browser.main;

import com.giphy.browser.common.BaseItem;

import org.jetbrains.annotations.NotNull;

public class GifItem implements BaseItem {
    @NotNull
    private final String id;
    @NotNull
    private final String webp;
    @NotNull
    private final String width;
    @NotNull
    private final String height;

    public GifItem(@NotNull String id, @NotNull String webp, String width, String height) {
        this.id = id;
        this.webp = webp;
        this.width = width;
        this.height = height;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    public String getWebp() {
        return webp;
    }

    @NotNull
    public String getWidth() {
        return width;
    }

    @NotNull
    public String getHeight() {
        return height;
    }
}
