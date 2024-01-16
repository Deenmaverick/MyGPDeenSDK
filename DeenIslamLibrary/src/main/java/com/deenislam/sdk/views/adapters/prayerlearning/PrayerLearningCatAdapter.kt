package com.deenislam.sdk.views.adapters.prayerlearning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.PrayerLearningCallback
import com.deenislam.sdk.service.network.response.prayerlearning.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder

internal class PrayerLearningCatAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var data: List<Data> = arrayListOf()
    private val callback = CallBackProvider.get<PrayerLearningCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prayer_learning_cat, parent, false)
        )

    fun update(data: List<Data>)
    {
        this.data = data
        notifyItemRangeChanged(0,itemCount)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuIcon:AppCompatImageView = itemView.findViewById(R.id.menuIcon)
        private val menuTitile:AppCompatTextView = itemView.findViewById(R.id.menuTitile)

        override fun onBind(position: Int) {
            super.onBind(position)

            val data = data[position]
            menuIcon.imageLoad(BASE_CONTENT_URL_SGP+data.ImageUrl, placeholder_1_1 = true)
            menuTitile.text = data.Category

            itemView.setOnClickListener {
                callback?.catClicked(data)
            }
        }
    }
}