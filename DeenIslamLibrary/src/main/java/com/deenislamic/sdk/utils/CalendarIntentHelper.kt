package com.deenislamic.sdk.utils

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal class CalendarIntentHelper(private val context: Context) {
    fun addEventToDefaultCalendar(eventName: String, dateTimeString: String) {
        val startDate = convertDateStringToMillis(dateTimeString)

        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate)
            .putExtra(CalendarContract.Events.TITLE, eventName)
            .putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)

        context.startActivity(intent)
    }

    private fun convertDateStringToMillis(dateTimeString: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = sdf.parse(dateTimeString)
        return date?.time ?: 0L
    }
}