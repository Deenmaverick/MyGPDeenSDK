package com.deenislamic.sdk.views.adapters.prayerlearning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class ListMenuAdapter(private val items: List<Item>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<HorizontalCardListCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_menuv2, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val ivEvent: AppCompatImageView = itemView.findViewById(R.id.ivEvent)
        private val menuName: AppCompatTextView = itemView.findViewById(R.id.menuName)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = items[position]

            if(getdata.imageurl1.isNotEmpty()) {
                ivEvent.imageLoad(
                    url = BASE_CONTENT_URL_SGP + getdata.imageurl1,
                    placeholder_1_1 = true
                )
                ivEvent.show()
            }else
                ivEvent.hide()

            menuName.text = getdata.ArabicText

            itemView.setOnClickListener {
                callback?.patchItemClicked(getdata)
            }

        }
    }
}