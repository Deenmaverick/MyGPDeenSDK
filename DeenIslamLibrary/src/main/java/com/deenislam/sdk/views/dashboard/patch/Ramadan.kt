package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class Ramadan {

    companion object
    {
        var instance: Ramadan? = null
    }

    fun getInstance(): Ramadan {
        if (instance == null)
            instance = Ramadan()

        return instance as Ramadan
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?) {

    }
}