package com.deenislamic.sdk.views.adapters.podcast;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.libs.advertisement.Advertisement
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.PATCH_COMMON_CARD_LIST
import com.deenislamic.sdk.utils.PATCH_MEDIUM_CARD_LIST
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.adapters.common.patch.MediumCardList
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.dashboard.patch.QuranicItem
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList

private const val PATCH_SINGLE_IMAGE = "SingleImage"
internal class LivePodcastMainAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var data:ArrayList<Data>  = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)

        when (data[viewType].AppDesign) {

            PATCH_MEDIUM_CARD_LIST-> {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {
                        onBindViewHolder(ViewHolder(main_view,true),viewType)
                    }
                }

            PATCH_COMMON_CARD_LIST-> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                    onBindViewHolder(ViewHolder(main_view,true),viewType)

                }
            }

            PATCH_SINGLE_IMAGE -> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

               /* 1->
                {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                        onBindViewHolder(ViewHolder(main_view,true),viewType)

                    }
                }
                2->
                {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {

                        onBindViewHolder(ViewHolder(main_view,true),viewType)


                    }
                }
                3->
                {
                    prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                        onBindViewHolder(ViewHolder(main_view,true),viewType)


                    }
                }*/
        }

        if(viewType == itemCount - 1) {

            val imageAd = Advertisement.getImageAd()
            imageAd?.let {
                prepareStubView<View>(rootview, R.layout.layout_quranic_v1) {
                    loadAdvertisement(this,it)
                }
            }

            prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_footer) {

            }
        }



        return  ViewHolder(main_view)
    }

    fun update(data: List<Data>) {

        this.data.addAll(data)
        notifyItemRangeInserted(itemCount, data.size)

    }

    private fun loadAdvertisement(
        itemview: View,
        data: com.deenislamic.sdk.service.network.response.advertisement.Data
    ) {
        data.let {
            (itemview.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
            val helper = QuranicItem(itemview)
            helper.loadImageAd(it)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View,private val loaded:Boolean = false) : BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded)
            {
                val getdata = data[viewtype]
                when(data[viewtype].AppDesign)
                {
                    PATCH_MEDIUM_CARD_LIST -> MediumCardList(itemView,getdata).load()
                    PATCH_COMMON_CARD_LIST -> {
                        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        SingleCardList(
                            itemView,
                            getdata
                        ).load()
                    }
                    PATCH_SINGLE_IMAGE -> {
                        val helper = QuranicItem(itemView)
                        helper.loadSingleImage(getdata.Items)
                    }
                    /*2 -> SingleCardItemPatch(itemView, "Live Quran Class").load()
                    3 -> LivePodcastListPatch(itemView).load()*/
                }
            }


        }
    }
}