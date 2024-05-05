package com.deenislam.sdk.views.hajjandumrah.patch;

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.views.adapters.common.gridmenu.MenuAdapter

internal class Menu(itemView: View, private val items: List<Item>) {

    private val listView: RecyclerView = itemView.findViewById(R.id.inf)

    init {

        listView.apply {
            layoutManager = GridLayoutManager(this.context,3)
            setPadding(16.dp,16.dp,16.dp,0)
            adapter = MenuAdapter(
                hajjAndUmrahMenuList = items,
                viewType = 3
            )
        }
    }

}