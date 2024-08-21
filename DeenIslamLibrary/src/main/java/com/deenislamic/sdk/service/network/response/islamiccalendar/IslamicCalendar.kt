package com.deenislamic.sdk.service.network.response.islamiccalendar

import androidx.annotation.Keep

@Keep
internal data class IslamicCalendar(
    val Success: Boolean,
    val Message: String,
    val TotalData: Int,
    val Data: List<CalendarData>
)

@Keep
internal data class CalendarData(
    val Date: String,
    val arabicDate: String,
    val Day: Int
)
