package com.deenislamic.sdk.views.quran.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.views.adapters.quran.RecentlyReadAdapter

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