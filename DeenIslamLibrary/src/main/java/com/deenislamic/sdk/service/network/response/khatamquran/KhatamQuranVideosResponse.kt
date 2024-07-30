package com.deenislamic.sdk.service.network.response.khatamquran


import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.service.network.response.paging.Pagination

@Keep
internal data class KhatamQuranVideosResponse(
    var Data: List<CommonCardData>?,
    var Message: String? = null,
    var Pagination: Pagination? = null,
    var Success: Boolean? = null
)
