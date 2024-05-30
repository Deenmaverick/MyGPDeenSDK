package com.deenislam.sdk.views.adapters.qurbani;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.QurbaniCallback
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder

internal class QurbaniMenuAdapter(private val data: List<Item>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<QurbaniCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.deen_item_menu_list, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val menuIcon:AppCompatImageView = itemView.findViewById(R.id.menuIcon)
        private val menuTitile: AppCompatTextView = itemView.findViewById(R.id.menuTitile)

        override fun onBind(position: Int) {
            super.onBind(position)

            val menuData = data[position]

            menuIcon.imageLoad(url = "$BASE_CONTENT_URL_SGP${menuData.imageurl1}", placeholder_1_1 = true)
            menuTitile.text = menuData.ArabicText

            itemView.setOnClickListener {
                callback?.selectedQurbaniCat(menuData)
            }

        }
    }
}