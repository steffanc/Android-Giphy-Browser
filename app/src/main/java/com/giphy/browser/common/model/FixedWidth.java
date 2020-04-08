package com.giphy.browser.common.model;

import com.squareup.moshi.Json;

public class FixedWidth {
    @Json(name = "webp")
    public String webp;

    @Json(name = "width")
    public String width;

    @Json(name = "height")
    public String height;
}
