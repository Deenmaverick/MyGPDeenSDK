package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class Greeting {

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


    }
}
