package com.deenislam.sdk.service.network.response.hadith.chapter

data class HadithChapterResponse(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)