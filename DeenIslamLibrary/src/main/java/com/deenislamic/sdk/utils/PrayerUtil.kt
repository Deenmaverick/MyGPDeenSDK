package com.deenislamic.sdk.utils

import android.content.Context
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.prayertimes.Data
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import java.text.SimpleDateFormat
import java.util.*


internal fun getPrayerTimeTagWise(
    prayer_tag: String,
    date: String,
    data: PrayerTimesResponse
):Long {

    val currentTime =
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(Date()).StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")


    return when (prayer_tag) {

        "pt1" -> {

            val fajr: Long = "$date ${data.Data.Fajr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  fajr-currentTime

        }

        "pt3" -> {
            val dhuhr =
                "$date ${data.Data.Juhr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  dhuhr- currentTime
        }

        "pt4" -> {
            val asr =
                "$date ${data.Data.Asr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  asr- currentTime
        }

        "pt5" -> {
            val maghrib =
                "$date ${data.Data.Magrib}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return maghrib - currentTime
        }

        "pt6" -> {
            val isha =
                "$date ${data.Data.Isha}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return isha - currentTime
        }
        else -> 0L
    }

}


internal fun getPrayerTimeWaktWise(
    prayer_tag: String,
    date: String,
    data: Data
):Long {

    val currentTime =
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH).format(Date()).StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")


    return when (prayer_tag) {

        "Fajr" -> {

            val fajr: Long = "$date ${data.Fajr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  fajr-currentTime

        }

        "Zuhr" -> {
            val dhuhr =
                "$date ${data.Juhr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  dhuhr- currentTime
        }

        "Asar" -> {
            val asr =
                "$date ${data.Asr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  asr- currentTime
        }

        "Maghrib" -> {
            val maghrib =
                "$date ${data.Magrib}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return maghrib - currentTime
        }

        "Isha" -> {
            val isha =
                "$date ${data.Isha}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return isha - currentTime
        }
        else -> 0L
    }

}

 fun get_prayer_tag_by_name(name:String): String =
    when(name)
    {
        "Fajr"-> "pt1"

        "Dhuhr"-> "pt3"

        "Asr"-> "pt4"

        "Maghrib"-> "pt5"

        "Isha"-> "pt6"

        "Tahajjud"-> "opt3"

        "Ishraq"-> "opt4"

        else -> ""

    }

fun get_prayer_name_by_tag(tag:String): String =
    when(tag)
    {
        "pt1"-> "Fajr"
        "pt2"-> "Sunrise"

        "pt3"-> "Dhuhr"

        "pt4"-> "Asr"

        "pt5"-> "Maghrib"

        "pt6"-> "Isha"

        "opt3"-> "Tahajjud"

        "opt1"-> "Suhoor"

        "opt4"-> "Ishraq"

        "opt2"-> "Iftaar"

        else -> ""

    }

fun String.checkCompulsoryprayerByName():Boolean =
    when(this)
    {
        "Fajr"-> true

        "Dhuhr"-> true

        "Asr"-> true

        "Maghrib"-> true

        "Isha"-> true

        else-> false
    }

fun String.checkCompulsoryprayerByTag():Boolean =
    when(this)
    {
        "pt1"-> true

        "pt3"-> true

        "pt4"-> true

        "pt5"-> true

        "pt6"-> true

        else-> false
    }

fun String.getWaktNameByTag():String =
    when(this)
    {

        "pt1"-> "Fajr"

        "pt3"-> "Zuhr"

        "pt4"-> "Asar"

        "pt5"-> "Maghrib"

        "pt6"-> "Isha"

        else-> ""
    }

fun Context.getArabicData(): String {
    val hijriCalendar = UmmalquraCalendar()

    val day = hijriCalendar.get(UmmalquraCalendar.DAY_OF_MONTH).toString().numberLocale()
    val month = resources.getStringArray(R.array.custom_months)[hijriCalendar.get(UmmalquraCalendar.MONTH)]
    val year = hijriCalendar.get(UmmalquraCalendar.YEAR).toString().numberLocale()

    return "$day $month $year Hijri"
}

fun isIndiaBangladeshPakistanTimeZone(): Boolean {
    val calendar = Calendar.getInstance()
    val timeZone = calendar.timeZone

    val timeZoneIds = setOf("Asia/Kolkata", "Asia/Dhaka", "Asia/Karachi")
    return timeZoneIds.contains(timeZone.id)
}
