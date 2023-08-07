package com.deenislam.sdk.service.network.response.prayertimes.tracker

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Asar: Boolean,
    val Fajr: Boolean,
    val Isha: Boolean,
    val Maghrib: Boolean,
    val Msisdn: String,
    val TrackingDate: String,
    val Zuhr: Boolean
)