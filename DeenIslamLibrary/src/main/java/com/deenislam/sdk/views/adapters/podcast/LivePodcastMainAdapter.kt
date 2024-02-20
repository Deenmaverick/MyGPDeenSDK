package com.deenislam.sdk.views.adapters.podcast;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.PATCH_COMMON_CARD_LIST
import com.deenislam.sdk.utils.PATCH_MEDIUM_CARD_LIST
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.adapters.common.patch.MediumCardList
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.dashboard.patch.SingleCardList

internal class LivePodcastMainAdapter() : RecyclerView.Adapter<BaseViewHolder>() {

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

        if(viewType>0 && viewType == itemCount - 1) {
            prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_footer) {

            }
        }



        return  ViewHolder(main_view)
    }

    fun update(data: List<Data>) {

        this.data.addAll(data)
        notifyItemRangeInserted(itemCount, data.size)

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
                    /*2 -> SingleCardItemPatch(itemView, "Live Quran Class").load()
                    3 -> LivePodcastListPatch(itemView).load()*/
                }
            }


        }
    }
}