package com.deenislam.sdk.views.adapters.islamicboyan

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.utils.dp

internal class BoyanScholarsPatch(
    view: View,
    private val title: String,
    private val isViewPager: Boolean = false,
    private val items: List<Item>
) {

    private  val listView: RecyclerView = view.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = view.findViewById(R.id.itemTitle)

    fun load()
    {

        itemTitle.text = title
        (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp


        listView.apply {
            setPadding(16.dp,0,8.dp,0)
            adapter = BoyanScholarsAdapter(items)
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }


    }

}