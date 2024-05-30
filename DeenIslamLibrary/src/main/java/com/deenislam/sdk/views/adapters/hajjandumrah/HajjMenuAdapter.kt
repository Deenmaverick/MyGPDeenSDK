package com.deenislam.sdk.views.adapters.hajjandumrah;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislam.sdk.views.base.BaseViewHolder

internal class HajjMenuAdapter (
    private val menuList: List<Item>? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

    // init view
    private var menuCallback = CallBackProvider.get<MenuCallback>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_menu_list,parent,false))

    override fun getItemCount(): Int = menuList?.size?:0


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    inner class ViewHolder(itemView: View) :BaseViewHolder(itemView)
    {
        private val menuIcon: AppCompatImageView = itemView.findViewById(R.id.menuIcon)
        private val menuTitile: AppCompatTextView = itemView.findViewById(R.id.menuTitile)

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position,viewtype)

            val getMenu  = menuList?.get(position)

            menuIcon.imageLoad(placeholder_1_1 = true, url =  BASE_CONTENT_URL_SGP +getMenu?.imageurl1)

            menuTitile.text = getMenu?.ArabicText

            itemView.setOnClickListener {
                menuCallback = CallBackProvider.get<MenuCallback>()
                getMenu?.Text?.let { it1 -> menuCallback?.menuClicked(it1,getMenu) }
            }
        }

    }

}