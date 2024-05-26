package com.deenislam.sdk.views.adapters.hajjandumrah

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.prepareStubView
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.views.dashboard.patch.DailyDua
import com.deenislam.sdk.views.dashboard.patch.SingleCardList
import com.deenislam.sdk.views.hajjandumrah.patch.Menu

internal class HajjAndUmrahHomePatchAdapter(private val patchData: List<com.deenislam.sdk.service.network.response.dashboard.Data>) : RecyclerView.Adapter<BaseViewHolder>() {
    private  val DESIGN_MENU = "menu"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)

        when (patchData[viewType].AppDesign) {

            DESIGN_MENU->
            {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            "Dua" -> {
                prepareStubView<View>(rootview, R.layout.dashboard_inc_item_horizontal_list) {
                    onBindViewHolder(ViewHolder(main_view, true), viewType)
                }
            }

            "CommonCardList" -> {
                prepareStubView<View>(rootview, R.layout.layout_horizontal_listview_v2) {
                    onBindViewHolder(ViewHolder(
                        itemView = main_view,
                        loaded = true
                    ), viewType)
                }
            }

        }

        return  ViewHolder(main_view)
    }

    override fun getItemCount(): Int = patchData.size

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
                val data = patchData[position]

                when(data.AppDesign)
                {
                    DESIGN_MENU -> Menu(itemView,data.Items)


                    "Dua" -> {
                        DailyDua(itemView, data)
                    }

                    "CommonCardList" -> {
                        //(itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        SingleCardList(
                            itemView,
                            data
                        ).load()
                    }
                }
            }
        }
    }
}