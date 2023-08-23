package com.deenislam.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.DashboardPatchCallback
import com.deenislam.sdk.service.network.response.dashboard.DailyDua
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder

internal class DailyDuaPatchAdapter(
    private val dashboardPatchCallback: DashboardPatchCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var dailyDuaData:ArrayList<DailyDua> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_dua_home, parent, false)
        )

    fun update(dailydua: List<DailyDua>)
    {
        dailyDuaData.clear()
        dailyDuaData.addAll(dailydua)
        notifyItemChanged(dailydua.size-1)
    }

    override fun getItemCount(): Int = dailyDuaData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val dailyDuaImg:AppCompatImageView = itemView.findViewById(R.id.dailyDuaImg)

        override fun onBind(position: Int) {
            super.onBind(position)

            itemView.setOnClickListener {
                dashboardPatchCallback.dashboardPatchClickd("DailyDua")
            }
            dailyDuaImg.imageLoad(dailyDuaData[position].contentBaseUrl+"/"+dailyDuaData[position].ImageUrl)

        }
    }
}