package com.deenislam.sdk.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.models.MenuModel
import com.deenislam.sdk.utils.AsyncViewStub
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

private const val ONBOARDING_MENU = 1
private const val DASHBOARD_MENU = 2
internal class MenuAdapter(
     private val menuList:ArrayList<MenuModel>,
     private val viewType: Int = 1,
     private val callback: favMenuCallback? = null,
     private val menuCallback: MenuCallback? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

    // init view
    private var initView:Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(viewType)
        {
            ONBOARDING_MENU -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menulist_onboarding,parent,false))
            DASHBOARD_MENU -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu_list_dashboard,parent,false))
            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position));
    }

    private inline fun <T : View> prepareStubView(
        stub: AsyncViewStub,
        layoutID:Int,
        crossinline prepareBlock: T.() -> Unit
    ) {
        stub.layoutRes = layoutID
        val inflatedView = stub.inflatedId as? T

        if (inflatedView != null) {
            inflatedView.prepareBlock()
        } else {
            stub.inflate(AsyncLayoutInflater.OnInflateFinishedListener { view, _, _ ->
                (view as? T)?.prepareBlock()
            })
        }
    }


   inner class ViewHolder(itemView: View) :BaseViewHolder(itemView)
   {
       private lateinit var menuCardview:MaterialCardView
       private lateinit var menuIcon: AppCompatImageView
       private lateinit var menuTitile:AppCompatTextView
       private lateinit var menuRadioBtn:RadioButton

       override fun onBind(position: Int, viewtype: Int) {
           super.onBind(position,viewtype)

           val getMenu: MenuModel = menuList[position]

           when (viewtype) {
                   ONBOARDING_MENU -> {

                       menuCardview = itemView.findViewById(R.id.menuCarview)
                       menuIcon = itemView.findViewById(R.id.menuIcon)
                       menuTitile = itemView.findViewById(R.id.menuTitile)
                       menuRadioBtn = itemView.findViewById(R.id.menuRadioBtn)

                       menuIcon.setImageDrawable(
                           AppCompatResources.getDrawable(
                               itemView.context,
                               getMenu.icon
                           )
                       )
                       menuTitile.text = getMenu.name

                       menuCardview.setOnClickListener {

                           if (menuRadioBtn.isChecked) {
                               callback?.unsetFavmenu(getMenu)
                               menuRadioBtn.isChecked = false
                               menuCardview.strokeWidth = 0
                           } else {
                               callback?.setFavmenu(getMenu)
                               menuRadioBtn.isChecked = true
                               menuCardview.strokeWidth = 2

                           }
                       }

                   }

                   DASHBOARD_MENU -> {

                       menuIcon = itemView.findViewById(R.id.menuIcon)
                       menuTitile = itemView.findViewById(R.id.menuTitile)

                       menuIcon.setImageDrawable(
                           AppCompatResources.getDrawable(
                               itemView.context,
                               getMenu.icon
                           )
                       )

                       menuTitile.text = getMenu.name

                       itemView.setOnClickListener {
                           menuCallback?.menuClicked(getMenu.menuTag)
                       }

                   }
               }

       }

   }

}

internal interface favMenuCallback
{
    fun setFavmenu(menu:MenuModel)
    fun unsetFavmenu(menu:MenuModel)
}

internal interface MenuCallback
{
    fun menuClicked(tag:String)
}