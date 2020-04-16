package com.giphy.browser.common.model

import com.squareup.moshi.Json

data class FixedWidth(
    @field:Json(name = "webp") val webp: String,
    @field:Json(name = "width") val width: String,
    @field:Json(name = "height") val height: String
)
