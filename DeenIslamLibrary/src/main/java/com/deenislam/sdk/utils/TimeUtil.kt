package com.deenislam.sdk.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.convertDateTime(pattern:String = "dd/M/yyyy hh:mm:ss aa",day:Int=0):Long
{
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH) // or you can add before dd/M/yyyy
    var convertedDateTime = sdf.parse(this)

    if(day!=0)
    {
        try {
            val c = Calendar.getInstance()
            c.time = convertedDateTime
            c.add(Calendar.DAY_OF_YEAR, day)
            val newDate = Date(c.timeInMillis)
            return newDate.time
        }
        catch (_:java.lang.Exception)
        {

        }
    }

    return convertedDateTime.time
}

fun generateUniqueNumber(): Int {
    return System.currentTimeMillis().toInt()
}

fun Long.DayDiffForRamadan(): String {
    val days = (this / (1000 * 60 * 60 * 24))


    var txtDays = days.toString()

    if (days < 10)
        txtDays = "0$days"


    return "${txtDays}"
}


fun Long.TimeDiffForRamadan(): String {
    val days = (this / (1000 * 60 * 60 * 24))
    val remainingHours = (this / (1000 * 60 * 60)) % 24
    val remainingMinutes = (this / (1000 * 60)) % 60

    var txtDays = days.toString()
    var txtHours = remainingHours.toString()
    var txtMinutes = remainingMinutes.toString()

    if (days < 10)
        txtDays = "0$days"

    if (remainingHours < 10)
        txtHours = "0$remainingHours"

    if (remainingMinutes < 10)
        txtMinutes = "0$remainingMinutes"

    return "${txtDays}:${txtHours}:${txtMinutes}"
}

fun Long.getMillis30DaysRamadan(): Long {
    val days = this / (1000 * 60 * 60 * 24)

    return if (days >= 30) {
        0
    } else {
        // If less than 30 days, calculate remaining milliseconds to reach 30 days
        val remainingDays = 30 - days
        val remainingMilliseconds = remainingDays * 24 * 60 * 60 * 1000 + (this % (24 * 60 * 60 * 1000))
        remainingMilliseconds
    }
}