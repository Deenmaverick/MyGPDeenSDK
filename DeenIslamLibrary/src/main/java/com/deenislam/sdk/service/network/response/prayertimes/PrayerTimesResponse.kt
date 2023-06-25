package com.deenislam.sdk.service.network.response.prayertimes

import com.deenislam.sdk.service.database.entity.PrayerTimes

data class PrayerTimesResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)