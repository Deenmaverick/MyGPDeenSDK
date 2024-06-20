package com.deenislamic.sdk.service.network.response.islamimasail.catlist

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.paging.Pagination

@Keep
internal data class IslamiMasailCatResponse(
    val Data: List<Data>,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean
)