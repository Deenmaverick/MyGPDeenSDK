package com.deenislam.sdk.views.qurbani.patch

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.views.adapters.qurbani.QurbaniMenuAdapter

internal class QurbaniMenuPatch(itemView: View, items: List<Item>) {

    private val listview: RecyclerView = itemView.findViewById(R.id.inf)

    init {

        listview.apply {
            adapter = QurbaniMenuAdapter(items)
            layoutManager = GridLayoutManager(this.context,2)
        }
    }

}