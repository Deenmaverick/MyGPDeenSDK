package com.deenislamic.sdk.service.network.response.ramadan.calendar

import androidx.annotation.Keep

@Keep
internal data class Calander(
    val TrackingDate: String,
    var isTracked: Int,
    val arabicDate:String
)