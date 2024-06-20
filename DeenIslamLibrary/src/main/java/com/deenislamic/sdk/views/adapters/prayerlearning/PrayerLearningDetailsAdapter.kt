package com.deenislamic.sdk.views.adapters.prayerlearning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.prayerlearning.visualization.Content
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class PrayerLearningDetailsAdapter(private val content: List<Content>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prayer_learning_details, parent, false)
        )

    override fun getItemCount(): Int = content.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val banner:AppCompatImageView = itemView.findViewById(R.id.banner)
        private val txtcontent:AppCompatTextView = itemView.findViewById(R.id.content)
        override fun onBind(position: Int) {
            super.onBind(position)

            val data = content[absoluteAdapterPosition]

            banner.imageLoad(BASE_CONTENT_URL_SGP+data.ImageUrl, placeholder_1_1 = true)
            txtcontent.text = data.Text

        }
    }
}