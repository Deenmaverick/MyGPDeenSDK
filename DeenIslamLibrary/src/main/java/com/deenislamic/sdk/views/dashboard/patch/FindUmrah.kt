package com.deenislamic.sdk.views.dashboard.patch;

import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class FindUmrah {

    companion object
    {
        var instance: FindUmrah? = null
    }

    fun getInstance(): FindUmrah {
        if (instance == null)
            instance = FindUmrah()

        return instance as FindUmrah
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?) {

    }
}