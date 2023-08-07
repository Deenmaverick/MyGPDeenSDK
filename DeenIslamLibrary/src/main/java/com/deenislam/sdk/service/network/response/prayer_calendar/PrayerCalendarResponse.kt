package com.deenislam.sdk.service.network.response.prayer_calendar

import androidx.annotation.Keep

@Keep
internal data class PrayerCalendarResponse(
    val Data: ArrayList<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)