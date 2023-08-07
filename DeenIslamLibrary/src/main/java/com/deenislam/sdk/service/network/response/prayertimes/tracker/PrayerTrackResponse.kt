package com.deenislam.sdk.service.network.response.prayertimes.tracker

import androidx.annotation.Keep

@Keep
internal data class PrayerTrackResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)