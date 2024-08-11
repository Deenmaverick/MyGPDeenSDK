package com.deenislamic.sdk.views.adapters.islamicname;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.islamicname.IslamicNameCallback
import com.deenislamic.sdk.service.network.response.islamicname.IslamicNameHomeResponse
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class IslamicNameHomeAdapter(
    private val islamicNames: List<IslamicNameHomeResponse.Data.Item>
) : RecyclerView.Adapter<BaseViewHolder>() {
    private val callback = CallBackProvider.get<IslamicNameCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_islamic_name_home, parent, false)
        )

    override fun getItemCount(): Int = islamicNames.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuName: AppCompatTextView = itemView.findViewById(R.id.menuName)
        override fun onBind(position: Int) {
            super.onBind(position)

            val listItem = islamicNames.get(position)
            menuName.text = listItem.ArabicText

            itemView.setOnClickListener {

                callback?.onItemClick(islamicNames[absoluteAdapterPosition])
            }

        }
    }
}