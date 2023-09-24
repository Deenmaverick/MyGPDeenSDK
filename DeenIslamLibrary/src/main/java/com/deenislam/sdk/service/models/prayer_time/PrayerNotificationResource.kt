package com.deenislam.sdk.service.models.prayer_time

import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.network.response.prayertimes.tracker.Data

internal  interface PrayerNotificationResource
{
    data class notificationData(val data: PrayerNotification?): PrayerNotificationResource
    data class setNotification(val value: Int,val data: ArrayList<PrayerNotification>):
        PrayerNotificationResource
    data class dateWiseNotificationData(val data: ArrayList<PrayerNotification>):
        PrayerNotificationResource

    object prayerTrackFailed: PrayerNotificationResource
    object NotificationStateRequired:PrayerNotificationResource
    data class prayerTrackData(val data: Data) :PrayerNotificationResource

}