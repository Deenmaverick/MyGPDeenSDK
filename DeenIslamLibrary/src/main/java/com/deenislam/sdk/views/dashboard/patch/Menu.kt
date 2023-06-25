package com.deenislam.sdk.views.dashboard.patch;

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.views.adapters.MenuAdapter
import com.deenislam.sdk.views.base.BaseMenu

internal class Menu {

    companion object
    {
        var instance: Menu? = null
    }

    fun getInstance(): Menu {
        if (instance == null)
            instance = Menu()

        Log.e("MENU_CLASS",instance.toString())

        return instance as Menu
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?) {

        val dashboardRecycleMenu:RecyclerView = widget.findViewById(R.id.dashboard_recycle_menu)

            val menu = BaseMenu().getInstance().getDashboardMenu()

            dashboardRecycleMenu.apply {
                    adapter = MenuAdapter(menu,2)
                    viewPool?.let { setRecycledViewPool(it) }
                }



    }
}