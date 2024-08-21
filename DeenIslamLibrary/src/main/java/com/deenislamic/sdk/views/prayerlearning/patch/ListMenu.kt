package com.deenislamic.sdk.views.prayerlearning.patch

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.views.adapters.prayerlearning.ListMenuAdapter

internal class ListMenu(itemView: View, private val items: List<Item>) {

    private val listView: RecyclerView = itemView.findViewById(R.id.listview)

    init {

        listView.apply {
            setPadding(16.dp,0,16.dp,0)
            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.VERTICAL,false)
            adapter = ListMenuAdapter(items)
        }
    }

}