package com.deenislam.sdk.views.adapters.islamicboyan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.LivePodcastCallback
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.base.BaseViewHolder

internal class ScholarsAdapter(): RecyclerView.Adapter<BaseViewHolder>() {

    private val podcastCallback = CallBackProvider.get<LivePodcastCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_live_podcast_series, parent, false)
    )

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {

    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)

            itemView.setOnClickListener {
                podcastCallback?.offlinePodcastItemClicked()
            }

        }
    }
}