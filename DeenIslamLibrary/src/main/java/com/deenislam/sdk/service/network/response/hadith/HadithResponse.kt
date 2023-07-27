package com.deenislam.sdk.service.network.response.hadith

import com.deenislam.sdk.service.network.response.hadith.Data

internal data class HadithResponse(
    val `data`: List<Data>,
    val limit: Int,
    val next: Any,
    val previous: Any,
    val total: Int
)