package com.deenislam.sdk.service.network.response.hadith.preview

import androidx.annotation.Keep

@Keep
internal data class HadithPreviewResponse(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)