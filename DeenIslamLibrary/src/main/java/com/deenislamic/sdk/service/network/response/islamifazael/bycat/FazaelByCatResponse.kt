package com.deenislamic.sdk.service.network.response.islamifazael.bycat

import androidx.annotation.Keep
import com.deenislamic.sdk.service.network.response.paging.Pagination

@Keep
internal data class FazaelByCatResponse(
    val Data: List<FazaelDataItem>,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean
)