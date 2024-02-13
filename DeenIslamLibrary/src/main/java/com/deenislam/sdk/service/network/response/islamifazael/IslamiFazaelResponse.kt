package com.deenislam.sdk.service.network.response.islamifazael

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.paging.Pagination

@Keep
internal data class IslamiFazaelResponse(
    val Data: List<Data>,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean
)