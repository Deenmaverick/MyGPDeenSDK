package com.deenislamic.sdk.service.models

import com.deenislamic.sdk.service.models.prayer_time.PrayerCalendarResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislamic.sdk.service.models.quran.AlQuranResource
import com.deenislamic.sdk.service.models.quran.SurahResource
import com.deenislamic.sdk.service.models.quran.learning.QuranLearningResource
import com.deenislamic.sdk.service.models.quran.quranplayer.ThemeResource

internal interface CommonResource
{
    object API_CALL_FAILED:CommonResource, PrayerTimeResource, PrayerCalendarResource, SurahResource,
        AlQuranResource, HadithResource, DailyDuaResource, ZakatResource, IslamicNameResource,
        DashboardResource, Allah99NameResource, IslamicEducationVideoResource, IslamicEventsResource,
        SubCatCardListResource, PrayerLearningResource, KhatamQuranVideoResource,
        QuranLearningResource, PaymentResource, RamadanResource, IslamiFazaelResource,
        IslamiMasailResource, SubscriptionResource, PodcastResource, HajjAndUmrahResource,
        BoyanResource, QurbaniResource

    object LOADING:CommonResource
    object EMPTY:CommonResource, PrayerCalendarResource, PrayerTimeResource,
        PrayerNotificationResource, AlQuranResource, SurahResource, HadithResource, DailyDuaResource,
        ZakatResource, IslamicNameResource, IslamicEducationVideoResource, IslamicEventsResource,
        SubCatCardListResource, PrayerLearningResource, KhatamQuranVideoResource, RamadanResource,
        IslamiFazaelResource, IslamiMasailResource, PodcastResource, HajjAndUmrahResource,
        BoyanResource, QurbaniResource

    object ACTION_API_CALL_FAILED:CommonResource, DailyDuaResource, IslamicNameResource,
        HadithResource, PrayerNotificationResource

    object CLEAR:CommonResource, ZakatResource, PrayerNotificationResource, SettingResource,
        DailyDuaResource, IslamicNameResource, HadithResource, KhatamQuranVideoResource,
        QuranLearningResource, PaymentResource, RamadanResource, IslamiMasailResource, ThemeResource,
        SubscriptionResource
}