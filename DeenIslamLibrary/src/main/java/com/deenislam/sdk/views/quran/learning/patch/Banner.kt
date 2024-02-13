package com.deenislam.sdk.views.quran.learning.patch;

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.DotsIndicatorDecoration
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.views.adapters.CarouselAdapter

internal class Banner(
    itemView: View,
    private val isViewPager: Boolean = false,
    private val items: List<Item>) {

    private  val listView: RecyclerView = itemView.findViewById(R.id.inf)

    fun load()
    {
        listView.apply {
            adapter = CarouselAdapter(items)
            onFlingListener = null
            PagerSnapHelper().attachToRecyclerView(listView)
            val radius = 6
            val dotsHeight = 56
            addItemDecoration(
                DotsIndicatorDecoration(
                    radius,
                    radius * 4,
                    dotsHeight,
                    ContextCompat.getColor(listView.context, R.color.deen_gray),
                    ContextCompat.getColor(listView.context, R.color.deen_txt_black)
                )
            )

            if(isViewPager)
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }
    }
}