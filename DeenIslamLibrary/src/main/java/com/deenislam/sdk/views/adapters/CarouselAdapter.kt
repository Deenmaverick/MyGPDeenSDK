package com.deenislam.sdk.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Qibla
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder

internal class CarouselAdapter: RecyclerView.Adapter<BaseViewHolder>() {

    private var adbanner: ArrayList<Qibla>  = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_carousel, parent, false)
        )

    fun update(adbanner: List<Qibla>)
    {
        this.adbanner.clear()
        this.adbanner.addAll(adbanner)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = adbanner.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    internal  inner class ViewHolder(itemView: View) :BaseViewHolder(itemView)
    {
        private val banner:AppCompatImageView = itemView.findViewById(R.id.banner)
        override fun onBind(position: Int) {
            super.onBind(position)

            val data = adbanner[position]
            banner.imageLoad(data.contentBaseUrl+"/"+data.imageurl1)
        }
    }
}