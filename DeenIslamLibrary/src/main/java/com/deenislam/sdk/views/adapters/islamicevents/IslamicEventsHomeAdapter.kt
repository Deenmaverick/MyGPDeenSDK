package com.deenislam.sdk.views.adapters.islamicevents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.islamicevent.IslamicEventCallback
import com.deenislam.sdk.service.network.response.islamicevent.IslamicEventListResponse.Data
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.base.BaseViewHolder

internal class IslamicEventsHomeAdapter(private val eventList: List<Data>) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val callback = CallBackProvider.get<IslamicEventCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_islamic_event_home, parent, false)
        )

    override fun getItemCount() = eventList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position, getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuName: AppCompatTextView = itemView.findViewById(R.id.menuName)
        private val ivEvent: AppCompatImageView = itemView.findViewById(R.id.ivEvent)

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            menuName.text = eventList.get(absoluteAdapterPosition).category
            //ivEvent.imageLoad(BASE_CONTENT_URL_SGP + eventList.get(absoluteAdapterPosition).imageurl)

            itemView.setOnClickListener {
                callback?.eventCatItemClick(eventList[absoluteAdapterPosition])
            }

        }
    }
}
