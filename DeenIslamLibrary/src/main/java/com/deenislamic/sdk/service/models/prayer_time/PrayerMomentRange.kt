package com.deenislamic.sdk.service.models.prayer_time

import androidx.annotation.Keep

@Keep
internal data class PrayerMomentRange(
    var MomentName: String,
    var StartTime: String,
    var EndTime: String,
    var NextPrayerName:String,
    var nextPrayerTimeCount:Long,
    var forbidden1: (Long) -> Boolean = { false },
    var forbidden2: (Long) -> Boolean = { false },
    var forbidden3: (Long) -> Boolean = { false },
    var chasht: (Long) -> Boolean = { false }
)
