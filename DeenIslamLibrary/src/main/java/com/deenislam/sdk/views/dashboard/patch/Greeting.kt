package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse

internal class Greeting {

    private var greeting_txt:AppCompatTextView ? =null

    companion object
    {
        var instance: Greeting? =null
    }
    fun getInstance(): Greeting
    {
        if(instance == null)
            instance = Greeting()

        return instance as Greeting
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?)
    {
        greeting_txt = widget.findViewById(R.id.greeting_txt)

    }

    fun update(data: PrayerTimesResponse)
    {
        instance?.greeting_txt?.text = data.Data.wish
    }
}
