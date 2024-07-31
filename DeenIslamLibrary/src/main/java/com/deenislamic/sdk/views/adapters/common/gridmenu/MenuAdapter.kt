package com.deenislamic.sdk.views.adapters.common.gridmenu

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.BasicCardListCallback
import com.deenislamic.sdk.service.models.MenuModel
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

private const val ONBOARDING_MENU = 1
private const val DASHBOARD_MENU = 2
private const val HAJJ_AND_UMRAH_MENU = 3
private const val HAJJ_STEP_SELECTION_MENU = 4
internal class MenuAdapter(
    private val menuList: List<Item>? = null,
    private val onBoardingMenuList: ArrayList<MenuModel> ? = null,
    private val hajjAndUmrahMenuList: List<Item> ? = null,
    private val hajjGuideStepList: List<Data>? = null,
    private val viewType: Int = 1
) : RecyclerView.Adapter<BaseViewHolder>() {

    // init view
    private var isShowMoreMenu = false
    private var menuCallback = CallBackProvider.get<MenuCallback>()
    private val basicCardListCallback = CallBackProvider.get<BasicCardListCallback>()

    var previousSelectedMenuIndex: Int = -1  // add this instance variable to keep track of the previous position
    private var selectedMenuIndex = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(viewType)
        {
            DASHBOARD_MENU -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu_list_dashboard,parent,false))
            HAJJ_STEP_SELECTION_MENU,HAJJ_AND_UMRAH_MENU -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.deen_item_menu_list,parent,false))
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
            4 -> hajjGuideStepList?.size?:0
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
            val oldselect = selectedMenuIndex
            selectedMenuIndex = pos  // update the current position
            notifyItemChanged(oldselect)  // refresh the previous position
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
       private val container: ConstraintLayout by lazy { itemView.findViewById(R.id.container) }


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

               HAJJ_STEP_SELECTION_MENU -> {
                   val getMenu  = hajjGuideStepList?.get(absoluteAdapterPosition)

                   val layoutParams = menuCardview.layoutParams
                   layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                   menuCardview.layoutParams = layoutParams

                  /* menuCardview.setPadding(16.dp,16.dp,16.dp,16.dp)

                   (menuIcon.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 12.dp
                   (menuIcon.layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart = 12.dp
                   (menuIcon.layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd = 12.dp
                   (menuIcon.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = 12.dp
*/
                   container.setPadding(12.dp,12.dp,12.dp,12.dp)

                   if(selectedMenuIndex == absoluteAdapterPosition) {
                       menuCardview.setCardBackgroundColor(
                           ContextCompat.getColor(
                               menuCardview.context,
                               R.color.deen_primary
                           )
                       )

                       menuIcon.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(menuIcon.context,R.color.deen_white), PorterDuff.Mode.SRC_IN)
                       //menuIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(menuIcon.context,R.color.deen_white))
                   }
                   else
                   {
                       menuCardview.setCardBackgroundColor(ContextCompat.getColor(menuCardview.context,R.color.deen_txt_ash))
                       menuIcon.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(menuIcon.context,R.color.deen_white_30), PorterDuff.Mode.SRC_IN)  //menuIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(menuIcon.context,R.color.deen_white_30))
                   }


                   menuIcon.imageLoad(BASE_CONTENT_URL_SGP+getMenu?.ImageUrl, placeholder_1_1 = true)

                   menuTitile.hide()

                   itemView.setOnClickListener {
                       if (getMenu != null) {
                           if(selectedMenuIndex!=absoluteAdapterPosition){
                               val oldselection = selectedMenuIndex
                               selectedMenuIndex = absoluteAdapterPosition
                               notifyItemChanged(oldselection)
                               notifyItemChanged(selectedMenuIndex)
                           }
                           basicCardListCallback?.basicCardListItemSelect(getMenu,absoluteAdapterPosition)
                       }
                   }
               }

           }
       }

   }

}

internal interface MenuCallback
{
    fun menuClicked(pagetag: String, getMenu: Item?=null){

    }
    fun menuClicked(pagetag: String){

    }
}