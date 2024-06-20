package com.deenislamic.sdk.service.network.response.ramadan

import androidx.annotation.Keep

@Keep
internal data class RamadanResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)