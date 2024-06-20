package com.deenislamic.sdk.service.network.response.prayertimes

import androidx.annotation.Keep

@Keep
internal data class PrayerTimesResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)