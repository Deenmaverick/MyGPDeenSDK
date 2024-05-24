package com.deenislam.sdk.service.network.response.prayertimes

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Asr: String,
    val Sunset:String,
    val Date: String,
    val Day: Int,
    val Fajr: String,
    val Isha: String,
    val Ishrak: String,
    val IslamicDate: String,
    val Juhr: String,
    val Magrib: String,
    val Noon: String,
    val Sehri: String,
    val Sunrise: String,
    val Tahajjut: String,
    val wish: String,
    val moment:String,
    val WaktTracker: List<WaktTracker>,
)