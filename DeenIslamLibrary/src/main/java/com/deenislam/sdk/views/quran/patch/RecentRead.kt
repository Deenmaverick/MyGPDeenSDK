package com.deenislam.sdk.views.quran.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.views.adapters.quran.RecentlyReadAdapter

internal class RecentRead(itemView: View, items: List<Item>) {

    private val listView: RecyclerView = itemView.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)

    init {

        itemTitle.text = itemTitle.context.getString(R.string.recently_read)

        itemTitle.setPadding(12.dp,0,0,0)

        listView.apply {
            setPadding(16.dp,0,8.dp,0)
            adapter = RecentlyReadAdapter(items)
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }
    }

}