package com.deenislamic.sdk.service.network.response.prayertimes.calendartracker

import androidx.annotation.Keep

@Keep
internal data class PrayerTrackerResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)
