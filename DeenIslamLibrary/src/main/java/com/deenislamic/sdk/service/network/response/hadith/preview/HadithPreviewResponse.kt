package com.deenislamic.sdk.service.network.response.hadith.preview

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.paging.Pagination

@Keep
internal data class HadithPreviewResponse(
    val Data: List<Data>?,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean,
    val TotalData: Int
)