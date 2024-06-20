package com.deenislamic.sdk.views.podcast.patch;

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.podcast.LivePodcastRecentAdapter

internal class LivePodcastRecentPatch(
    view: View,
    private val title: String,
    private val isViewPager: Boolean = false,
    private val items: List<Item>,
    private val iconDrawable:Int = -1,
    private val isShowPlayIcon:Boolean = true
) {

    private  val listView: RecyclerView = view.findViewById(R.id.listview)
    private val itemTitle:AppCompatTextView = view.findViewById(R.id.itemTitle)
    private val icon:AppCompatImageView = view.findViewById(R.id.icon)

    fun load()
    {

        itemTitle.text = title

        if(iconDrawable!=-1)
        {
            icon.load(iconDrawable)
            icon.show()
            (icon.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp
            (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 8.dp

        }
        else
            (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp



        listView.apply {
            setPadding(16.dp,0,8.dp,0)
            adapter = LivePodcastRecentAdapter(items,isShowPlayIcon)
            if(isViewPager)
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }


    }
}