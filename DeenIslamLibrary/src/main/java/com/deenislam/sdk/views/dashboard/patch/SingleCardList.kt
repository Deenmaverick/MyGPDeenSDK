package com.deenislam.sdk.views.dashboard.patch

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.SpanningLinearLayoutManager
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.utils.transformCommonCardListPatchModel
import com.deenislam.sdk.views.adapters.podcast.LivePodcastRecentAdapter

internal class SingleCardList(
    view: View,
    private val data: Data,
) {

    private  val listView: RecyclerView = view.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = view.findViewById(R.id.itemTitle)
    private val icon: AppCompatImageView = view.findViewById(R.id.icon)

    fun load()
    {

        if(!data.Logo.isNullOrEmpty()){
            icon.show()
            icon.imageLoad(url = BASE_CONTENT_URL_SGP+data.Logo, placeholder_1_1 = true)
            (icon.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp
            (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 8.dp
        }
        else
            (itemTitle.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 16.dp

        itemTitle.text = data.Title

        listView.apply {
            layoutManager = SpanningLinearLayoutManager(this.context,RecyclerView.HORIZONTAL,false,264.dp)
            setPadding(16.dp,0,8.dp,0)
            adapter = LivePodcastRecentAdapter(
                items = data.Items.map { transformCommonCardListPatchModel(
                    it,
                    itemTitle = it.ArabicText,
                    itemMidContent = it.Text,
                    itemSubTitle = it.Reference,
                    itemBtnText = it.FeatureButton.toString()) },
            )
        }


    }
}