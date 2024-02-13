package com.deenislam.sdk.service.network.response.ramadan.calendar

import androidx.annotation.Keep

@Keep
internal data class Calander(
    val TrackingDate: String,
    var isTracked: Int
)