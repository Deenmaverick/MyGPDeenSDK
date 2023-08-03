package com.deenislam.sdk.views.dashboard.patch;

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Service
import com.deenislam.sdk.utils.GridSpacingItemDecoration
import com.deenislam.sdk.views.adapters.MenuAdapter
import com.deenislam.sdk.views.adapters.MenuCallback
import com.deenislam.sdk.views.base.BaseMenu
import com.google.gson.Gson

internal class Menu {

    private var dashboardRecycleMenu:RecyclerView ? = null
    private var menuAdapter:MenuAdapter ? = null
    companion object
    {
        var instance: Menu? = null
    }

    fun getInstance(): Menu {
        if (instance == null)
            instance = Menu()

        return instance as Menu
    }

    fun load(widget: View, viewPool: RecyclerView.RecycledViewPool?, menuCallback: MenuCallback?) {

        instance?.dashboardRecycleMenu = widget.findViewById(R.id.dashboard_recycle_menu)

       // val menu = BaseMenu().getInstance().getDashboardMenu(widget.context)

        val spacing = widget.context.resources.getDimensionPixelSize(R.dimen.space_10)
        val spanCount = 4
        val includeEdge = true

        instance?.menuAdapter = MenuAdapter(2, menuCallback = menuCallback)


        instance?.dashboardRecycleMenu?.apply {
            adapter = instance?.menuAdapter
            addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
            viewPool?.let { setRecycledViewPool(it) }
        }

    }

    fun update(services: List<Service>)
    {
        Log.e("UPDATE_MENU", instance?.menuAdapter.toString())

        instance?.menuAdapter?.update(services)
    }
}