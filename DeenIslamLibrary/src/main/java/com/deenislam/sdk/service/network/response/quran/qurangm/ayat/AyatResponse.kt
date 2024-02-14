package com.deenislam.sdk.service.network.response.quran.qurangm.ayat

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.paging.Pagination

@Keep
internal data class AyatResponse(
    val Data: Data,
    val Message: String,
    val Pagination: Pagination,
    val Success: Boolean
)