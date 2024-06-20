package com.deenislamic.sdk.views.islamiceducationvideo.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.islamiceducationvideo.EducationGridAdapter
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.hide

internal class EducationVideoGrid(
    itemView: View,
    pageTitle: String,
    private val educationVideos: List<CommonCardData>
) {

    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private val listview: RecyclerView = itemView.findViewById(R.id.listview)

    init {


        if(pageTitle.isNotEmpty()) {
            itemTitle.text = pageTitle

            itemTitle.show()
            itemTitle.setPadding(8.dp, 0, 16.dp, 0)
        }
        else
            itemTitle.hide()

        listview.apply {
            setPadding(8.dp,0,8.dp,0)
            layoutManager = GridLayoutManager(this.context, 2)
            adapter = EducationGridAdapter(listview, educationVideos)
        }
    }

}