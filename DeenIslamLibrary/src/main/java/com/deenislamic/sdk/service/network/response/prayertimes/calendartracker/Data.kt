package com.deenislamic.sdk.service.network.response.prayertimes.calendartracker

import androidx.annotation.Keep

@Keep
internal data class Data(

    val tracker: List<Tracker>,
    val prayerTime: PrayerTime
    /*    val Asar: Boolean,
        val Fajr: Boolean,
        val Isha: Boolean,
        val Maghrib: Boolean,
        val Msisdn: String,
        val TrackingDate: String,
        val Zuhr: Boolean*/
){

    @Keep
    data class Tracker(
        val Msisdn: String,
        var TrackingDate: String,
        val ArabicDate: String,
        var Fajr: Boolean,
        var Zuhr: Boolean,
        var Asar: Boolean,
        var Maghrib: Boolean,
        var Isha: Boolean
    )

    @Keep
    data class PrayerTime(
        val Date: String,
        val IslamicDate: String,
        val BanglaDate: String,
        val Day: Int,
        val Sehri: String,
        val Fajr: String,
        val Sunrise: String,
        val Noon: String,
        val Juhr: String,
        val Asr: String,
        val Sunset: String,
        val Magrib: String,
        val Isha: String,
        val Tahajjut: String,
        val Ishrak: String,
        val wish: String,
        val moment: String
    )
}