package com.deenislam.sdk.views.khatamquran.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.common.CommonCardData
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.adapters.common.CommonCardAdapter

internal class KhatamEQuranVideos(
    itemView: View,
    pageTitle: String,
    val khatamQuranVideos: ArrayList<CommonCardData> = ArrayList()
) {
    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private val listview: RecyclerView = itemView.findViewById(R.id.listview)

    init {
        itemTitle.text = pageTitle
        itemTitle.show()
        itemTitle.setPadding(8.dp, 0, 16.dp, 0)
        listview.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CommonCardAdapter(isShowPlayIcon = true, isShowProgress = false, data = khatamQuranVideos.map { transformData(it) } as ArrayList<CommonCardData>, bannerSize = 1280, itemMarginLeft = 0.dp, itemMarginRight = 0.dp,itemPaddingBottom = 12.dp)
        }
    }
    private fun transformData(newDataModel: CommonCardData?) : CommonCardData{
        return CommonCardData(
            title = newDataModel?.title,
            imageurl = newDataModel?.imageurl,
            DurationInSec = newDataModel?.DurationInSec!!,
            DurationWatched = newDataModel.DurationWatched,
            videourl = newDataModel.videourl,
            reference = null,
            customReference = newDataModel.reference
        )
    }
}