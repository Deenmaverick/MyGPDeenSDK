package com.deenislamic.sdk.views.prayertimes.patch

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.views.adapters.prayer_times.WidgetForbiddenTimes

internal class ForbiddenTimes {

    private var widgetForbiddenTimes:WidgetForbiddenTimes ? =null
    companion object
    {
        var instance: ForbiddenTimes? = null
    }

    fun getInstance(): ForbiddenTimes {
        if (instance == null)
            instance = ForbiddenTimes()
        return instance as ForbiddenTimes
    }

    fun load(view: View)
    {

        val title: AppCompatTextView = view.findViewById(R.id.title)
        val prayerTimes: RecyclerView = view.findViewById(R.id.prayertime)

        title.text = view.context.getString(R.string.forbidden_time)

        instance?.widgetForbiddenTimes = WidgetForbiddenTimes()

        prayerTimes.apply {
            adapter = instance?.widgetForbiddenTimes
        }

    }

    fun update(prayerTimesResponse: PrayerTimesResponse)
    {
        instance?.widgetForbiddenTimes?.updateData(prayerTimesResponse)

    }

}