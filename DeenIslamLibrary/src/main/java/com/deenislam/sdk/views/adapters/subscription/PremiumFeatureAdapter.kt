package com.deenislam.sdk.views.adapters.subscription;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.views.base.BaseViewHolder

internal class PremiumFeatureAdapter(private val premiumFeatures: List<com.deenislam.sdk.service.network.response.subscription.PremiumFeature>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_premium_feature, parent, false)
        )

    override fun getItemCount(): Int = premiumFeatures.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val title:AppCompatTextView = itemView.findViewById(R.id.title)
        private val subText:AppCompatTextView = itemView.findViewById(R.id.subText)
        override fun onBind(position: Int) {
            super.onBind(position)
            val getdata = premiumFeatures[position]
            title.text = getdata.featureName
            subText.text = getdata.featureSubText
        }
    }
}