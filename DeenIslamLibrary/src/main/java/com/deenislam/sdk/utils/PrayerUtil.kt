package com.deenislam.sdk.utils

import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import java.text.SimpleDateFormat
import java.util.*

internal fun getPrayerTimeTagWise(
    prayer_tag: String,
    date: String,
    data: PrayerTimesResponse
):Long {

    val currentTime =
        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()).StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

       val nightTime =  "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())} 23:59:59".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

    /*   if(currentTime>=nightTime)
    Log.e("PRAYER_NT", "$currentTime ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}  $date ${data.Data.Isha}")
*/

    return when (prayer_tag) {

        "pt1" -> {

            val fajr: Long
            if(currentTime<nightTime)
                fajr =
                    "$date ${data.Data.Fajr}".convertDateTime("dd/MM/yyyy HH:mm:ss",1)
            else
             fajr =
                "$date ${data.Data.Fajr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  fajr-currentTime

        }

        "pt2" -> {

            val sunrise =
                "$date ${data.Data.Sunrise}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  sunrise - currentTime

        }

        "pt3" -> {
            val dhuhr =
                "$date ${data.Data.Juhr}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  dhuhr- currentTime
        }

        "pt4" -> {
            val asr =
                "$date ${data.Data.Asr}".StringTimeToMillisecond("dd/M/yyyy HH:mm:ss")

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

        "opt1" -> {
            val tahajjut: Long
            if(currentTime<nightTime)
             tahajjut =
                "$date ${data.Data.Tahajjut}".convertDateTime("dd/MM/yyyy HH:mm:ss",1)
            else
                tahajjut =
                    "$date ${data.Data.Tahajjut}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  tahajjut-currentTime
        }

        "opt2" -> {

            val suhoor: Long
            if(currentTime<nightTime)
                suhoor =
                    "$date ${data.Data.Sehri}".convertDateTime("dd/MM/yyyy HH:mm:ss",1)
                else
             suhoor =
                "$date ${data.Data.Sehri}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  suhoor - currentTime
        }

        "opt3" -> {
            val ishrak: Long
            if(currentTime<nightTime)
                ishrak =
                    "$date ${data.Data.Ishrak}".convertDateTime("dd/MM/yyyy HH:mm:ss",1)
                else
             ishrak =
                "$date ${data.Data.Ishrak}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  ishrak - currentTime
        }

        "opt4" -> {
            val iftar =
                "$date ${data.Data.Magrib}".StringTimeToMillisecond("dd/MM/yyyy HH:mm:ss")

            return  iftar - currentTime
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

        "Tahajjud"-> "opt1"

        "Ishraq"-> "opt3"

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

        "opt1"-> "Tahajjud"

        "opt2"-> "Suhoor"

        "opt3"-> "Ishraq"

        "opt4"-> "Iftaar"

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
