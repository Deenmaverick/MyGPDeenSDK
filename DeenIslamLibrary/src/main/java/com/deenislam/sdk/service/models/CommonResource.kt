package com.deenislam.sdk.service.models

import com.deenislam.sdk.service.models.prayer_time.PrayerCalendarResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.models.quran.AlQuranResource
import com.deenislam.sdk.service.models.quran.SurahResource

internal interface CommonResource
{
    object API_CALL_FAILED:CommonResource, PrayerTimeResource, PrayerCalendarResource, SurahResource,
        AlQuranResource, HadithResource, DailyDuaResource, ZakatResource

    object LOADING:CommonResource
    object EMPTY:CommonResource, PrayerCalendarResource, PrayerTimeResource,
        PrayerNotificationResource, AlQuranResource, SurahResource, HadithResource, DailyDuaResource,
        ZakatResource

    object ACTION_API_CALL_FAILED:CommonResource, DailyDuaResource
    object CLEAR:CommonResource, ZakatResource, PrayerNotificationResource, SettingResource,
        DailyDuaResource
}