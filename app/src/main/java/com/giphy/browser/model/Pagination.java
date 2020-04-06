package com.giphy.browser.model;

import com.squareup.moshi.Json;

public class Pagination {
    @Json(name = "offset")
    public int offset = 0;

    @Json(name = "total_count")
    public int totalCount = 0;

    @Json(name = "count")
    public int count = 0;
}
