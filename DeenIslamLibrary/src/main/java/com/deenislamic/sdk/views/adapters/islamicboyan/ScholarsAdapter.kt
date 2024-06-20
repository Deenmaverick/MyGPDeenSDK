package com.deenislamic.sdk.views.adapters.islamicboyan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.LivePodcastCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseViewHolder

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