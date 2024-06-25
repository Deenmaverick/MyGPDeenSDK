package com.deenislamic.sdk.views.adapters.islamicname;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicname.IslamicNameCatCallback
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameCategoriesResponse
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class IslamicNameCatAdapter(
    private val islamicNamesCat: List<IslamicNameCategoriesResponse.Data>
) : RecyclerView.Adapter<BaseViewHolder>() {
    private val callback = CallBackProvider.get<IslamicNameCatCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_islamic_event_home, parent, false)
        )

    override fun getItemCount(): Int = islamicNamesCat.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuName: AppCompatTextView = itemView.findViewById(R.id.menuName)
        override fun onBind(position: Int) {
            super.onBind(position)

            val listItem = islamicNamesCat[position]
            menuName.text = listItem.title
            itemView.setOnClickListener {

                callback?.onCatItemClick(islamicNamesCat[absoluteAdapterPosition])
            }

        }
    }
}