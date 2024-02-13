package com.deenislam.sdk.service.network.response.ramadan

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.ramadan.Data

@Keep
internal data class RamadanResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)