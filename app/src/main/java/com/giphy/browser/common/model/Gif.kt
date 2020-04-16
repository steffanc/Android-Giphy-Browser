package com.giphy.browser.common.model

import com.squareup.moshi.Json

data class Gif(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "images") val images: Images
)
