package com.deenislam.sdk.views.prayertimes.patch

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.views.adapters.prayer_times.WidgetOtherPrayerTimes
import com.deenislam.sdk.views.adapters.prayer_times.prayerTimeAdapterCallback

class OtherPrayerTimes {

    private var widgetOtherPrayerTimes:WidgetOtherPrayerTimes ? =null

    companion object
    {
        var instance: OtherPrayerTimes? = null
    }

    fun getInstance(): OtherPrayerTimes {
        if (instance == null)
            instance = OtherPrayerTimes()
        return instance as OtherPrayerTimes
    }

    fun load(view: View, callback: prayerTimeAdapterCallback?)
    {
        val title:AppCompatTextView = view.findViewById(R.id.title)
        val prayerTimes: RecyclerView = view.findViewById(R.id.prayertime)
        title.text = "Other Prayers"

        instance?.widgetOtherPrayerTimes = WidgetOtherPrayerTimes(callback)

        prayerTimes.apply {
            adapter = instance?.widgetOtherPrayerTimes
        }
    }

    fun update(
        prayerTimesResponse: PrayerTimesResponse,
        dateWisePrayerNotificationData: ArrayList<PrayerNotification>?,
        prayerMomentRangeData: PrayerMomentRange?
    )
    {
        instance?.widgetOtherPrayerTimes?.updateData(prayerTimesResponse,dateWisePrayerNotificationData,prayerMomentRangeData)
    }

}