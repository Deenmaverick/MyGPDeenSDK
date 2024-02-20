package com.deenislam.sdk.views.adapters.common.patch

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.adapters.podcast.LivePodcastSeriesAdapter

internal class MediumCardList(widget: View, private val getdata: Data) {

    private  val listView: RecyclerView = widget.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = widget.findViewById(R.id.itemTitle)
    private val icon: AppCompatImageView = widget.findViewById(R.id.icon)

    fun load()
    {

        if(!getdata.Logo.isNullOrEmpty()){
            icon.show()
            icon.imageLoad(url = BASE_CONTENT_URL_SGP +getdata.Logo, placeholder_1_1 = true)
            (icon.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp
            (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 8.dp
        }
        else
            (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp

        itemTitle.text = getdata.Title

        listView.apply {
            setPadding(16.dp,0,8.dp,0)
            adapter = LivePodcastSeriesAdapter(getdata.Items)
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }

    }
}