package com.deenislamic.sdk.views.adapters.islamicboyan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class BoyanScholarsAdapter(
    private val items: List<Item>
): RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<HorizontalCardListCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_live_podcast_series, parent, false)
    )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val headerImg: AppCompatImageView by lazy { itemView.findViewById(R.id.headerImg) }
        private val pageTitle: AppCompatTextView by lazy { itemView.findViewById(R.id.pageTitle) }
        private val subText: AppCompatTextView by lazy { itemView.findViewById(R.id.subText) }
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = items[position]

            headerImg.imageLoad(url = getData.contentBaseUrl+"/"+getData.imageurl1, placeholder_16_9 = true)

            pageTitle.text = getData.Text
            subText.text = getData.DuaId.toString().numberLocale() + " বয়ান"

            itemView.setOnClickListener {
                callback = CallBackProvider.get<HorizontalCardListCallback>()
                callback?.smallCardPatchItemClicked(getData)
            }
        }
    }
}