package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse

internal class Greeting {

    private var greeting_txt:AppCompatTextView ? =null
    private var greetingIcon:AppCompatImageView ? = null

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
        greetingIcon = widget.findViewById(R.id.greetingIcon)

        //greetingIcon.setBackgroundResource(ContextCompat.gete)

    }

    fun update(data: PrayerTimesResponse)
    {
        instance?.greeting_txt?.text = data.Data.wish

        when(data.Data.moment)
        {
            "Morning" -> instance?.greetingIcon?.setImageResource(R.drawable.ic_sunrise_fill)
            "Noon" -> instance?.greetingIcon?.setImageResource(R.drawable.ic_sun_fill)
            "Afternoon" -> instance?.greetingIcon?.setImageResource(R.drawable.ic_cloud_sun_fill)
            "Evening" -> instance?.greetingIcon?.setImageResource(R.drawable.ic_sunset_fill)
            "Night" -> instance?.greetingIcon?.setImageResource(R.drawable.ic_isha)

        }
    }
}
