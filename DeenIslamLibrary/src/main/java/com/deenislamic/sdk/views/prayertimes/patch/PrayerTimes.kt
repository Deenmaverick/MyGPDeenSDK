package com.deenislamic.sdk.views.prayertimes.patch

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislamic.sdk.views.adapters.prayer_times.WidgetPrayerTimes
import com.deenislamic.sdk.views.adapters.prayer_times.prayerTimeAdapterCallback

internal class PrayerTimes {

    private var widgetPrayerTimes:WidgetPrayerTimes ? =null
    private var prayerTimes:RecyclerView ? = null
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
        instance?.prayerTimes = view.findViewById(R.id.prayertime)
        title.text = view.context.resources.getString(R.string.prayer_times)

        instance?.widgetPrayerTimes = WidgetPrayerTimes(callback)

        instance?.prayerTimes?.apply {
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
       /* instance?.prayerTimes?.post {
            instance?.widgetPrayerTimes?.notifyDataSetChanged()
        }*/
    }

    fun updateTrackingData(data: Data)
    {
        instance?.widgetPrayerTimes?.updateTrackingData(data)
    }
}