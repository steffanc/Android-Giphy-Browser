package com.giphy.browser.common.model;

import com.squareup.moshi.Json;

public class Gif {
    @Json(name = "id")
    public String id;

    @Json(name = "images")
    public Images images;
}
