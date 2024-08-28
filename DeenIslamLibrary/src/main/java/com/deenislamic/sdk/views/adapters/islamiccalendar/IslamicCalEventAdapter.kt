package com.deenislamic.sdk.views.adapters.islamiccalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamicCalEventCallback
import com.deenislamic.sdk.service.network.response.islamiccalendar.IslamicEvent
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.formateDateTime
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.monthNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

internal class IslamicCalEventAdapter(private val callback: IslamicCalEventCallback): RecyclerView.Adapter<BaseViewHolder>() {
    private val eventList: ArrayList<IslamicEvent> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event_cal, parent, false)
        )

    fun update(data: List<IslamicEvent>) {
        eventList.clear()
        eventList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = eventList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val tvCalendarTitle: AppCompatTextView = itemView.findViewById(R.id.tvCalendarTitle)
        private val tvCalendarEventDate: AppCompatTextView = itemView.findViewById(R.id.tvCalendarEventDate)
        private val tvCalendarDateLeft: AppCompatTextView = itemView.findViewById(R.id.tvCalendarDateLeft)
        private val imvBanner: AppCompatImageView = itemView.findViewById(R.id.imvBanner)
        private val imvCalendar: AppCompatImageView = itemView.findViewById(R.id.imvAddGoogleCalendar)
        private val tvDate: AppCompatTextView = itemView.findViewById(R.id.tvDate)
        private val tvMonth: AppCompatTextView = itemView.findViewById(R.id.tvMonth)
        private val parentCard: MaterialCardView = itemView.findViewById(R.id.parentLayout)


        override fun onBind(position: Int) {
            super.onBind(position)

            val event = eventList[position]

            val monthName = event.Date.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","MMMM")
            val dayOfMonth = event.Date.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","d")

            tvCalendarTitle.text = event.text
            tvCalendarEventDate.text = event.IslamicDate
            tvCalendarDateLeft.text = event.Status
            tvDate.text = dayOfMonth.numberLocale()
            tvMonth.text = monthName.monthNameLocale()
            imvBanner.imageLoad(BASE_CONTENT_URL_SGP +event.imageurl)

            tvCalendarDateLeft.setTextColor(ContextCompat.getColor(tvCalendarDateLeft.context,if(!event.isUpcoming) R.color.deen_primary else R.color.deen_txt_ash))

            imvCalendar.setOnClickListener {
                callback.itemPosition(event.text,event.Date)
            }

            parentCard.setOnClickListener{
                callback.itemClick(event)
            }
        }
    }
}
