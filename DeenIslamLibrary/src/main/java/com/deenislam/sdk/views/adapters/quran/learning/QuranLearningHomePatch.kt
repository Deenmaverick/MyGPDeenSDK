package com.deenislam.sdk.views.adapters.quran.learning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.ViewInflationListener
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.utils.transformCommonCardListPatchModel
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.podcast.patch.LivePodcastRecentPatch
import com.deenislam.sdk.views.quran.learning.patch.Banner

internal class QuranLearningHomePatch(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var viewInflationListener = CallBackProvider.get<ViewInflationListener>()
    private var inflatedViewCount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)

        when (data[viewType].Design) {
            "Banners" -> {

                prepareStubView<View>(rootView, R.layout.layout_horizontal_listview) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }

            "SeriesQuranLearnPatch"->
            {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }
           /* 2->
            {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.item_quranic_v2) {

                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }
            3->
            {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()

                }
            }*/
        }

        return ViewHolder(mainView)
    }

    private fun completeViewLoad() {
        inflatedViewCount++
        if (inflatedViewCount >= 2) {
            viewInflationListener?.onAllViewsInflated()
        }
        //viewInflationListener.onAllViewsInflated()
    }
    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    inner class ViewHolder(itemView: View, private val loaded: Boolean = false) :
        BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded)
            {
                val getData = data[position]
                when(data[viewtype].Design)
                {
                    "Banners" ->
                    {
                        Banner(itemView, items = getData.Items).load()
                    }
                    "SeriesQuranLearnPatch"->
                    {
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        LivePodcastRecentPatch(itemView,getData.Title,
                            items = getData.Items.map { transformCommonCardListPatchModel(it, itemTitle = it.MText, itemSubTitle = "", itemBtnText = it.Meaning) }
                        ).load()
                    }
                    /*"SeriesQuranLearnPatch"->
                    {
                        LivePodcastSingleItemPatch(
                            view = itemView,
                            title = "Live Quran Class",
                            isPlayBtn = true,
                            isLive = true,
                            isFooter = true
                        ).load()
                    }
                    3->
                    {
                        LivePodcastRecentPatch(itemView,"Quran Learning Series").load()
                    }*/
                }
            }
        }
    }
}