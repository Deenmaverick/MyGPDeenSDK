package com.deenislamic.sdk.views.prayertimes.patch

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.views.adapters.prayer_times.WidgetOtherPrayerTimes
import com.deenislamic.sdk.views.adapters.prayer_times.prayerTimeAdapterCallback

internal class OtherPrayerTimes {

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
        title.text = view.context.getString(R.string.other_prayers)

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