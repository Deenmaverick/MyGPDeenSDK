package com.deenislamic.sdk.views.adapters.prayerlearning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.prayerlearning.patch.GridMenuList
import com.deenislamic.sdk.views.prayerlearning.patch.ListMenu

internal class PrayerLearningCatAdapter(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(data[viewType].AppDesign)
        {
            "smenu" -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_horizontal_listview,parent,false))
            "lmenu" -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_horizontal_listview,parent,false))
            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }


    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position,viewtype)

            val getdata = data[viewtype]

            when(getdata.AppDesign){
                "smenu" -> GridMenuList(itemView,getdata.Items)
                "lmenu" -> ListMenu(itemView,getdata.Items)
            }
        }
    }
}