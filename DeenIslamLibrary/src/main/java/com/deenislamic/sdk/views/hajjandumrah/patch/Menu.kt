package com.deenislamic.sdk.views.hajjandumrah.patch;

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.views.adapters.hajjandumrah.HajjMenuAdapter

internal class Menu(itemView: View, private val items: List<Item>) {

    private val listView: RecyclerView = itemView.findViewById(R.id.inf)

    init {

        listView.apply {
            layoutManager = GridLayoutManager(this.context,3)
            setPadding(12.dp,12.dp,12.dp,0)
            adapter = HajjMenuAdapter( items)
        }
    }

}