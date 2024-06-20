package com.deenislamic.sdk.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QuranLearningCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class CarouselAdapter(private val items: List<Item>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<QuranLearningCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_carousel, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) :BaseViewHolder(itemView)
    {
        private val banner:AppCompatImageView = itemView.findViewById(R.id.banner)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = items[position]
            banner.imageLoad(url = getData.contentBaseUrl+"/"+getData.imageurl1, placeholder_16_9 = true)
            itemView.setOnClickListener {
                callback?.homePatchItemClicked(getData)
            }
        }
    }
}