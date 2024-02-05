package com.deenislam.sdk.views.dashboard.patch;

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.adapters.common.gridmenu.MenuAdapter
import com.google.android.material.button.MaterialButton

internal class Menu(private val widget: View,private val items: List<Item>) {

    private lateinit var seeMoreMenu: MaterialButton
    private var menuAdapter: MenuAdapter? = null

    init {
        load()
    }

    private fun load() {
        val dashboardRecycleMenu: RecyclerView = widget.findViewById(R.id.dashboard_recycle_menu)
        seeMoreMenu = widget.findViewById(R.id.seeMoreMenu)

        if (items.size > 8)
            seeMoreMenu.show()
        else
            seeMoreMenu.hide()

        menuAdapter = MenuAdapter(items, viewType = 2)

        dashboardRecycleMenu.adapter = menuAdapter

        seeMoreMenu.setOnClickListener {
            menuAdapter?.seeMoreMenu()
            if (menuAdapter?.isSeeMoreMenu() == true) {
                seeMoreMenu.text = seeMoreMenu.context.getString(R.string.see_less)
                seeMoreMenu.icon = ContextCompat.getDrawable(widget.context, R.drawable.deen_ic_dropdown_expand)
            } else {
                seeMoreMenu.text = seeMoreMenu.context.getString(R.string.see_more)
                seeMoreMenu.icon = ContextCompat.getDrawable(widget.context, R.drawable.ic_dropdown)
            }
        }
    }
}