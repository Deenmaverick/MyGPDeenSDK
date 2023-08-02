package com.deenislam.sdk.service.network.response.hadith

import androidx.annotation.Keep

@Keep
internal data class HadithResponse(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)