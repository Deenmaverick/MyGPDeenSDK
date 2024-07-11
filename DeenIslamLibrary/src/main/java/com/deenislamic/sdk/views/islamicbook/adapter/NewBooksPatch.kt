package com.deenislamic.sdk.views.islamicbook.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.islamicbook.BookDownloadPayload
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp

internal class NewBooksPatch(
    view: View,
    private val title: String,
    private val isViewPager: Boolean = false,
    private val items: List<Item>
) {

    private val listView: RecyclerView = view.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = view.findViewById(R.id.itemTitle)
    private var islamicBookHomeItemAdapter = IslamicBookHomeItemAdapter(items)

    fun load()
    {
        itemTitle.text = title
        Log.d("ThePdfHeaderHeader", title)
        (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp

        listView.apply {
            setPadding(16.dp,0,16.dp, 0)
            layoutManager = LinearLayoutManager(listView.context, LinearLayoutManager.VERTICAL, false)
            adapter = islamicBookHomeItemAdapter
            if(isViewPager)
                ViewPagerHorizontalRecyler().getInstance().load(this)
        }
    }

    fun update(payload: BookDownloadPayload, fileName: String?){
        val getPosition = islamicBookHomeItemAdapter.findPositionByData(fileName)

        if(getPosition!=-1) {
                islamicBookHomeItemAdapter.notifyItemChanged(getPosition, payload)
        }
    }
}