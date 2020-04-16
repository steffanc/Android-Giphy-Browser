package com.giphy.browser.common.model

import com.squareup.moshi.Json

data class Pagination(
    @field:Json(name = "offset") var offset: Int = 0,
    @field:Json(name = "total_count") var totalCount: Int = 0,
    @field:Json(name = "count") var count: Int = 0
) {
    fun nextOffset(): Int = offset + count
}
