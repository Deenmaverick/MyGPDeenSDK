package com.deenislam.sdk.service.models.prayer_time

internal data class PrayerMomentRange(
    val MomentName: String,
    val StartTime: String,
    val EndTime: String,
    val NextPrayerName:String,
    val nextPrayerTimeCount:Long
)
