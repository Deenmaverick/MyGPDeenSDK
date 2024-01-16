package com.deenislam.sdk.views.islamiceducationvideo.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.setStarMargin
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.adapters.common.CommonCardAdapter
import com.deenislam.sdk.service.network.response.common.CommonCardData

internal class IslamicEducationVideo(itemView: View, pageTitle: String, val recentWatchedList: ArrayList<CommonCardData> = ArrayList()) {

    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private val listview: RecyclerView = itemView.findViewById(R.id.listview)

    init {

        itemTitle.text = pageTitle

        itemTitle.show()
        itemTitle.setStarMargin(0.dp)
        itemTitle.setPadding(16.dp, 0, 16.dp, 0)

        listview.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CommonCardAdapter(264.dp, true, false, data = recentWatchedList, bannerSize = 1280, itemMarginLeft = 0.dp, itemMarginRight = 8.dp)
        }
    }

}