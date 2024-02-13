package com.deenislam.sdk.views.adapters.ramadan;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.RamadanCallback
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder

internal class RamadanPatchMenuAdapter(private val items: List<Item>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<RamadanCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prayer_learning_cat, parent, false)
        )


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuIcon: AppCompatImageView = itemView.findViewById(R.id.menuIcon)
        private val menuTitile: AppCompatTextView = itemView.findViewById(R.id.menuTitile)

        override fun onBind(position: Int) {
            super.onBind(position)

            val data = items[position]
            menuIcon.imageLoad(BASE_CONTENT_URL_SGP +data.imageurl1, placeholder_1_1 = true)
            menuTitile.text = data.ArabicText

            itemView.setOnClickListener {
                callback?.patchClicked(data)
            }
        }
    }
}