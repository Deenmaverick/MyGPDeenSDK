package com.deenislam.sdk.service.network.response.hadith.chapter

import androidx.annotation.Keep

@Keep
internal data class HadithChapterResponse(
    val `data`: List<Data>,
    val limit: Int,
    val next: Int,
    val previous: Any,
    val total: Int
)