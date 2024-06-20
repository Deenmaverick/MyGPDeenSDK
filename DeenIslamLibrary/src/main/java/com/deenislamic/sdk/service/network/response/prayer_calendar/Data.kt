package com.deenislamic.sdk.service.network.response.prayer_calendar

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Asr: String,
    val Date: String,
    val Day: Int,
    val Fajr: String,
    val Isha: String,
    val Ishrak: String,
    val Juhr: String,
    val Magrib: String,
    val Noon: String,
    val Sehri: String,
    val Sunrise: String,
    val Tahajjut: String
)