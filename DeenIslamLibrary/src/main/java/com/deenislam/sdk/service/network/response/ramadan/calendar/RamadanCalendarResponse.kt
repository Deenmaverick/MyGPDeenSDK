package com.deenislam.sdk.service.network.response.ramadan.calendar

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.ramadan.calendar.Data

@Keep
internal data class RamadanCalendarResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)