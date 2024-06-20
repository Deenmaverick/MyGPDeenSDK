package com.deenislamic.sdk.views.khatamquran.patch;

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.common.CommonCardData
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.setStarMargin
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.adapters.common.CommonCardAdapter

internal class KhatamQuranRecentVideo(itemView: View, pageTitle: String, buttonText: String, val recentWatchedList: ArrayList<CommonCardData> = ArrayList()) {

    private val itemTitle: AppCompatTextView = itemView.findViewById(R.id.itemTitle)
    private val listview: RecyclerView = itemView.findViewById(R.id.listview)

    init {

        itemTitle.text = pageTitle

        itemTitle.show()
        itemTitle.setStarMargin(0.dp)
        itemTitle.setPadding(16.dp, 0, 16.dp, 0)

        listview.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CommonCardAdapter(
                cardWidthDp = 264.dp,
                isShowPlayIcon = true,
                isShowProgress = true, data = recentWatchedList.map { transformData(it,buttonText) } as ArrayList<CommonCardData>, bannerSize = 1280, itemMarginLeft = 0.dp, itemMarginRight = 8.dp)
        }
    }
    private fun transformData(newDataModel: CommonCardData?, buttonText: String): CommonCardData {
        return CommonCardData(
            title = newDataModel?.title,
            imageurl = newDataModel?.imageurl,
            buttonTxt = buttonText,
            DurationInSec = newDataModel?.DurationInSec!!,
            DurationWatched = newDataModel.DurationWatched,
            videourl = newDataModel.videourl,
            category = "Recent",
            reference = null,
            customReference = newDataModel.reference
        )

    }
}