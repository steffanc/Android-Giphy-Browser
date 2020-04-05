package com.giphy.browser.network;

import com.giphy.browser.model.Gif;
import com.squareup.moshi.Json;

import java.util.List;

public class GifsResponse {
    @Json(name = "data") public List<Gif> data;
}
