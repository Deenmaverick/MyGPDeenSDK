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