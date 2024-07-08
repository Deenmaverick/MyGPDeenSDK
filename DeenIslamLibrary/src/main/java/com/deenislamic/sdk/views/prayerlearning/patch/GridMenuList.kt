package com.deenislamic.sdk.views.prayerlearning.patch

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.views.adapters.prayerlearning.GridMenuAdapter

internal class GridMenuList(itemView: View, private val items: List<Item>) {

    private val listView: RecyclerView = itemView.findViewById(R.id.listview)

    init {

        listView.apply {
            layoutManager = GridLayoutManager(this.context,2)
            adapter = GridMenuAdapter(items)
        }
    }

}