package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class DailyVerse {

    companion object
    {
        var instance: DailyVerse? = null
    }


    fun getInstance(): DailyVerse {
        if (instance == null)
            instance = DailyVerse()

        return instance as DailyVerse
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?) {

    }
}