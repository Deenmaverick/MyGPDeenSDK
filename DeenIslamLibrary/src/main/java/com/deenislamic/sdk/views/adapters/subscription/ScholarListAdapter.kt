package com.deenislamic.sdk.views.adapters.subscription;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.subscription.Scholar
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class ScholarListAdapter(private val scholars: List<Scholar>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_scholar_list, parent, false)
        )

    override fun getItemCount(): Int = scholars.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val ivProfile:AppCompatImageView = itemView.findViewById(R.id.ivProfile)
        private val tvName:AppCompatTextView = itemView.findViewById(R.id.tvName)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = scholars[position]

            ivProfile.imageLoad(
                    url = BASE_CONTENT_URL_SGP+getdata.ImageUrl,
                    placeholder_1_1 = true
                )

            tvName.text = getdata.Name

        }
    }
}