package com.deenislamic.sdk.views.adapters.islamicboyan

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ViewInflationListener
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.podcast.patch.LivePodcastRecentPatch

internal class IslamicBoyanHomeAdapter(
    private val data: List<Data>
): RecyclerView.Adapter<BaseViewHolder>() {

    private var viewInflationListener = CallBackProvider.get<ViewInflationListener>()
    private var inflatedViewCount: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val mainView = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootView: AsyncViewStub = mainView.findViewById(R.id.widget)

        when (data[viewType].Design) {

            "BoyanScholars"-> {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }

            "BoyanCategories"-> {
                prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(mainView,true),viewType)
                    completeViewLoad()
                }
            }

            "BoyanPopular"-> {
                 prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                     onBindViewHolder(ViewHolder(mainView,true),viewType)
                     completeViewLoad()
                 }
             }
        }

        if(viewType == itemCount - 1) {
            prepareStubView<View>(rootView.findViewById(R.id.widget),R.layout.layout_footer) {

            }
        }

        return ViewHolder(mainView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    private fun completeViewLoad() {
        inflatedViewCount++
        if (inflatedViewCount >= 3) {
            viewInflationListener?.onAllViewsInflated()
        }
        //viewInflationListener.onAllViewsInflated()
    }

    inner class ViewHolder(itemView: View, private val loaded: Boolean = false) :
        BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded)
            {

                val getData = data[position]

                Log.e("BoyanPopular",data[viewtype].Design)

                when(data[viewtype].Design)
                {

                    "BoyanScholars"-> {
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        BoyanScholarsPatch(itemView,getData.Title,items = getData.Items).load()
                    }

                    "BoyanCategories"-> {
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        BoyanCategoriesPatch(itemView,getData.Title,items = getData.Items).load()
                    }

                    "BoyanPopular"-> {
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        LivePodcastRecentPatch(
                            view = itemView,
                            title = getData.Title,
                            items = getData.Items.map { transformCommonCardListPatchModel(it, itemTitle = it.Title, itemSubTitle = "", itemBtnText = "") },
                            isShowPlayIcon = true).load()
                    }
                }
            }
        }
    }

}