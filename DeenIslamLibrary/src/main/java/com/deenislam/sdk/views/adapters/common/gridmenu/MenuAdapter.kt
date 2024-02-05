package com.deenislam.sdk.views.adapters.common.gridmenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.BasicCardListCallback
import com.deenislam.sdk.service.models.MenuModel
import com.deenislam.sdk.service.network.response.dashboard.Item
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

private const val ONBOARDING_MENU = 1
private const val DASHBOARD_MENU = 2
private const val HAJJ_AND_UMRAH_MENU = 3
private const val HAJJ_STEP_SELECTION_MENU = 4
internal class MenuAdapter(
    private val menuList: List<Item>? = null,
    private val onBoardingMenuList: ArrayList<MenuModel> ? = null,
    private val hajjAndUmrahMenuList: List<Item> ? = null,
    private val viewType: Int = 1
) : RecyclerView.Adapter<BaseViewHolder>() {

    // init view
    private var isShowMoreMenu = false
    private var menuCallback = CallBackProvider.get<MenuCallback>()
    private val basicCardListCallback = CallBackProvider.get<BasicCardListCallback>()

    var previousSelectedMenuIndex: Int = -1  // add this instance variable to keep track of the previous position
    private var selectedMenuIndex = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(viewType)
        {
            DASHBOARD_MENU -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu_list_dashboard,parent,false))
            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }

    override fun getItemCount(): Int {

        return when(viewType)
        {
            2 ->
            {
                if((menuList?.size ?: 0) > 8 && isShowMoreMenu)
                    menuList?.size?:0
                else if((menuList?.size ?: 0) > 8)
                    8
                else
                    menuList?.size?:0
            }

            3-> hajjAndUmrahMenuList?.size?:0
            else -> onBoardingMenuList?.size?:0
        }

    }

    fun seeMoreMenu()
    {
        isShowMoreMenu = !isShowMoreMenu
        notifyDataSetChanged()

    }

    fun updateSelectedIndex(pos: Int) {
        // Check if there was any previously selected position
        if (selectedMenuIndex != -1) {
            previousSelectedMenuIndex = selectedMenuIndex  // store the current position to previous before updating it
            notifyItemChanged(previousSelectedMenuIndex)  // refresh the previous position
        }

        selectedMenuIndex = pos  // update the current position
        notifyItemChanged(selectedMenuIndex)  // refresh the current position
    }

    fun isSeeMoreMenu() = isShowMoreMenu

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


   inner class ViewHolder(itemView: View) :BaseViewHolder(itemView)
   {
       private val menuCardview:MaterialCardView by lazy { itemView.findViewById(R.id.menuCarview) }
       private val menuIcon: AppCompatImageView by lazy { itemView.findViewById(R.id.menuIcon) }
       private val menuTitile:AppCompatTextView by lazy { itemView.findViewById(R.id.menuTitile) }

       override fun onBind(position: Int, viewtype: Int) {
           super.onBind(position,viewtype)

           when (viewtype) {

                   DASHBOARD_MENU -> {

                       val getMenu: Item? = menuList?.get(absoluteAdapterPosition)

                       menuIcon.imageLoad(getMenu?.contentBaseUrl+"/"+getMenu?.imageurl1)

                       menuTitile.text = getMenu?.ArabicText

                       itemView.setOnClickListener {
                           menuCallback = CallBackProvider.get<MenuCallback>()
                           getMenu?.Text?.let { it1 -> menuCallback?.menuClicked(it1, getMenu) }
                       }

                   }


               HAJJ_AND_UMRAH_MENU -> {

                   val getMenu  = hajjAndUmrahMenuList?.get(position)

                   menuIcon.imageLoad(placeholder_1_1 = true, url =  BASE_CONTENT_URL_SGP+getMenu?.imageurl1)

                   menuTitile.text = getMenu?.ArabicText

                   itemView.setOnClickListener {
                       menuCallback = CallBackProvider.get<MenuCallback>()
                       getMenu?.Text?.let { it1 -> menuCallback?.menuClicked(it1,getMenu) }
                   }

               }

           }
       }

   }

}

internal interface MenuCallback
{
    fun menuClicked(pagetag: String, getMenu: Item?=null)
}