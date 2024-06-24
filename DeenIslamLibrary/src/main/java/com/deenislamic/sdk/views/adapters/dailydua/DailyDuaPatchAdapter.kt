package com.deenislamic.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.DashboardPatchCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class DailyDuaPatchAdapter(
    private val dashboardPatchCallback: DashboardPatchCallback?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var dailyDuaData:ArrayList<Item> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_dua_home, parent, false)
        )

    fun update(dailydua: List<Item>)
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
        private val shareBtn: MaterialButton = itemView.findViewById(R.id.shareBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            val data = dailyDuaData[position]
            itemView.setOnClickListener {
                dashboardPatchCallback?.dashboardPatchClickd(data.ContentType, data)
            }
            dailyDuaImg.imageLoad(data.contentBaseUrl+"/"+data.imageurl1, placeholder_1_1 =
            true)

            shareBtn.setOnClickListener {

                dailyDuaImg.doOnLayout {
                    val drawable = dailyDuaImg.drawable
                    // Convert the Drawable to a Bitmap
                    val bitmap = drawable.toBitmap()
                    dashboardPatchCallback?.shareImage(bitmap)
                }

            }
        }
    }
}