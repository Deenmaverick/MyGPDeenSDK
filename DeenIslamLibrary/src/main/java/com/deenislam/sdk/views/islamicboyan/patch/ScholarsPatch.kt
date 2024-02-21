package com.deenislam.sdk.views.islamicboyan.patch

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislam.sdk.utils.dp

internal class ScholarsPatch(widget: View) {

    private val listView: RecyclerView = widget.findViewById(R.id.inf)

    fun load() {
        listView.apply {
            setPadding(16.dp,16.dp,16.dp,0)
            //adapter = LivePodcastSeriesAdapter()
            ViewPagerHorizontalRecyler().getInstance().load(this)
        }

    }

}