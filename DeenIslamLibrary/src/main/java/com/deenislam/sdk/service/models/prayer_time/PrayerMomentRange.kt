package com.deenislam.sdk.service.models.prayer_time

import androidx.annotation.Keep

@Keep
internal data class PrayerMomentRange(
    val MomentName: String,
    val StartTime: String,
    val EndTime: String,
    val NextPrayerName:String,
    val nextPrayerTimeCount:Long
)
