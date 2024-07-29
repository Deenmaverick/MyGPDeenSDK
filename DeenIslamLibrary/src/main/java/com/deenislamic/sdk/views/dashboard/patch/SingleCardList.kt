package com.deenislamic.sdk.views.dashboard.patch

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.SpaceItemDecoration
import com.deenislamic.sdk.utils.SpanningLinearLayoutManager
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.transformCommonCardListPatchModel
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.adapters.podcast.LivePodcastRecentAdapter

internal class SingleCardList(
view: View,
private val data: Data,
private val orientation:Int = RecyclerView.HORIZONTAL
) {

    private  val listView: RecyclerView = view.findViewById(R.id.listview)
    private val itemTitle: AppCompatTextView = view.findViewById(R.id.itemTitle)
    private val icon: AppCompatImageView = view.findViewById(R.id.icon)
    private val seeAllBtn:AppCompatTextView = view.findViewById(R.id.seeAllBtn)
    private var callback = CallBackProvider.get<MenuCallback>()

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

        if(data.Items.size>1) {
            seeAllBtn.show()
            seeAllBtn.setOnClickListener {
                callback = CallBackProvider.get<MenuCallback>()
                callback?.menuClicked(data.FeatureType.toString(),null)
            }
        }

        listView.apply {
            layoutManager = SpanningLinearLayoutManager(this.context,orientation,false,264.dp)
            setPadding(16.dp,0,8.dp,0)
            if(orientation == RecyclerView.VERTICAL){
                addItemDecoration(SpaceItemDecoration(spaceHeight = 12.dp))
            }
            setHasFixedSize(true)
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