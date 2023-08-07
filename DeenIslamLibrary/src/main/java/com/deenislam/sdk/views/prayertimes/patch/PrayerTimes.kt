package com.deenislam.sdk.views.prayertimes.patch

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislam.sdk.views.adapters.prayer_times.WidgetPrayerTimes
import com.deenislam.sdk.views.adapters.prayer_times.prayerTimeAdapterCallback

internal class PrayerTimes {

    private var widgetPrayerTimes:WidgetPrayerTimes ? =null
    companion object
    {
        var instance: PrayerTimes? = null
    }

    fun getInstance(): PrayerTimes {

        if (instance == null)
            instance = PrayerTimes()

        return instance as PrayerTimes
    }


    fun load(view:View, callback: prayerTimeAdapterCallback?)
    {
        val title:AppCompatTextView = view.findViewById(R.id.title)
        val prayerTimes:RecyclerView = view.findViewById(R.id.prayertime)
        title.text = view.context.resources.getString(R.string.prayer_times)

        instance?.widgetPrayerTimes = WidgetPrayerTimes(callback)

        prayerTimes.apply {
            adapter = instance?.widgetPrayerTimes
        }
    }

    fun update(
        prayerTimesResponse: PrayerTimesResponse,
        dateWisePrayerNotificationData: ArrayList<PrayerNotification>?,
        prayerMomentRangeData: PrayerMomentRange?
    )
    {
        instance?.widgetPrayerTimes?.updateData(prayerTimesResponse,dateWisePrayerNotificationData,prayerMomentRangeData)
    }

    fun updateTrackingData(data: Data)
    {
        instance?.widgetPrayerTimes?.updateTrackingData(data)
    }
}