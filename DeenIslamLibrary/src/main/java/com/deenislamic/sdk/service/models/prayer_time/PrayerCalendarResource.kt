package com.deenislamic.sdk.service.models.prayer_time

import com.deenislamic.sdk.service.network.response.prayer_calendar.Data

internal  interface PrayerCalendarResource
{
    data class monthlyData(val data:ArrayList<Data>): PrayerCalendarResource
}