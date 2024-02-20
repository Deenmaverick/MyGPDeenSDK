package com.deenislam.sdk.views.adapters.podcast;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView

internal class LivePodcastSeriesAdapter(private val getdata: List<Item>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<HorizontalCardListCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_live_podcast_series, parent, false)
        )

    override fun getItemCount(): Int = getdata.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val headerImg: ShapeableImageView = itemView.findViewById(R.id.headerImg)
        private val pageTitle:AppCompatTextView = itemView.findViewById(R.id.pageTitle)
        private val subText:AppCompatTextView = itemView.findViewById(R.id.subText)
        override fun onBind(position: Int) {
            super.onBind(position)

            val data = getdata[absoluteAdapterPosition]

            headerImg.imageLoad(url = BASE_CONTENT_URL_SGP+data.imageurl1, placeholder_1_1 = true)
            pageTitle.text = data.ArabicText
            subText.text = data.Reference

            itemView.setOnClickListener {
                callback?.patchItemClicked(data)
            }

        }
    }
}