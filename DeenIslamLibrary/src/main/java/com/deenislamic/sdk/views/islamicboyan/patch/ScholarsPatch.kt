package com.deenislamic.sdk.views.islamicboyan.patch

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.ViewPagerHorizontalRecyler
import com.deenislamic.sdk.utils.dp

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