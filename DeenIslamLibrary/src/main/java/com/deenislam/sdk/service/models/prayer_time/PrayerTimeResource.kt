package com.deenislam.sdk.service.models.prayer_time

import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse

internal interface PrayerTimeResource
{
    data class postPrayerTime(val data:PrayerTimesResponse): PrayerTimeResource
    object prayerTimeEmpty: PrayerTimeResource
    data class selectedState(val state: StateModel) :PrayerTimeResource
}