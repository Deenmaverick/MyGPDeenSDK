package com.deenislam.sdk.service.network.response.hadith.chapter

import androidx.annotation.Keep

@Keep
internal data class HadithChapterResponse(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)