package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.libs.trackercalendar.CalendarTrackerDay

internal interface PrayerTrackerCallback {
    fun prayerTrack(prayer_tag: String, date: String, isPrayed: Boolean)
    fun prayerDate(day: CalendarTrackerDay)
}