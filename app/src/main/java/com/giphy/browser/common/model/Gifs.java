package com.giphy.browser.common.model;

import com.squareup.moshi.Json;

import java.util.List;

public class Gifs {
    @Json(name = "data")
    public List<Gif> data;

    @Json(name = "pagination")
    public Pagination pagination;
}
