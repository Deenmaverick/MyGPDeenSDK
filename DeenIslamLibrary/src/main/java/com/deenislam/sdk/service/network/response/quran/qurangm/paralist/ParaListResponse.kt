package com.deenislam.sdk.service.network.response.quran.qurangm.paralist

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.paging.Pagination

@Keep
internal data class ParaListResponse(
    val Data: List<Data>,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean
)