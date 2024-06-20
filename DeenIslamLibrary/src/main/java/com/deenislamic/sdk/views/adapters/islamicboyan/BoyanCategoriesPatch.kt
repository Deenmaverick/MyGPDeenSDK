package com.deenislamic.sdk.views.adapters.islamicboyan

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp

internal class BoyanCategoriesPatch(
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
            adapter = BoyanCategoryAdapter(items)
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            if(isViewPager)
                ViewPagerHorizontalRecyler().getInstance().load(this)
        }


    }
}