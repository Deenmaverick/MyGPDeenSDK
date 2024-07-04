package com.deenislamic.sdk.utils

import java.text.ParseException
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

fun String.stringTimeToEpochTime(pattern: String = "HH:mm:ss"): Long {
    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    try {
        val date = sdf.parse(this)
        // Calculate milliseconds since midnight
        val calendar = Calendar.getInstance().apply { time = date!! }
        val milliseconds = calendar.get(Calendar.HOUR_OF_DAY) * 3600000L + // Hours to milliseconds
                calendar.get(Calendar.MINUTE) * 60000L +       // Minutes to milliseconds
                calendar.get(Calendar.SECOND) * 1000L          // Seconds to milliseconds
        return milliseconds
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0
}


fun Long.epochTimeToStringTime(pattern: String = "hh:mm a"): String {
    // Calculate hours, minutes, and seconds from milliseconds since midnight
    val hours = this / 3600000L
    val remainingAfterHours = this % 3600000L
    val minutes = remainingAfterHours / 60000L
    val remainingAfterMinutes = remainingAfterHours % 60000L
    val seconds = remainingAfterMinutes / 1000L

    // Create a calendar instance to format the time string
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hours.toInt())
        set(Calendar.MINUTE, minutes.toInt())
        set(Calendar.SECOND, seconds.toInt())
    }

    val sdf = SimpleDateFormat(pattern, Locale.ENGLISH)
    return sdf.format(calendar.time)
}

fun isTimeInRange(currentTime: Long, startTime: Long, endTime: Long): Boolean {
    val currentEpoch = currentTime
    val startEpoch = startTime.minus(2000)
    val endEpoch = endTime.minus(2000)

    return if (startEpoch < endEpoch) {
        // Normal case, same day
        currentEpoch in startEpoch..endEpoch
    } else {
        // Crosses midnight
        currentEpoch >= startEpoch || currentEpoch <= endEpoch
    }
}

fun getEpochTimeDifference(time1: Long, time2: Long): Long {
    val time1Epoch = time1
    val time2Epoch = time2

    // Handle times crossing midnight
    return if (time1Epoch <= time2Epoch) {
        time2Epoch - time1Epoch
    } else {
        (24 * 3600000L - time1Epoch) + time2Epoch
    }
}