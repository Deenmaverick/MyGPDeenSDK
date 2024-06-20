package com.deenislamic.sdk.service.network.response.ramadan.calendar

import androidx.annotation.Keep

@Keep
internal data class RamadanCalendarResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)