package com.deenislam.sdk.service.models.prayer_time

import com.deenislam.sdk.service.network.response.prayer_calendar.Data

 interface PrayerCalendarResource
{
    data class monthlyData(val data:ArrayList<Data>): PrayerCalendarResource
}