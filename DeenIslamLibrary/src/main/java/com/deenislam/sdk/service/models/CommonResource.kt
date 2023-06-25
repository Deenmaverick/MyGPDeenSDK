package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.models.prayer_time.PrayerCalendarResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.models.quran.SurahResource

internal interface CommonResource
{
    object API_CALL_FAILED:CommonResource, PrayerTimeResource, PrayerCalendarResource, SurahResource,
        AlQuranResource

    object LOADING:CommonResource
    object EMPTY:CommonResource, PrayerCalendarResource, PrayerTimeResource,
        PrayerNotificationResource, AlQuranResource
}