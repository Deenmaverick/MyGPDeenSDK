package com.deenislam.sdk.service.network.response.zakat.nisab

import androidx.annotation.Keep

@Keep
internal data class NisabResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)