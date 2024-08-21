package com.deenislamic.sdk.views.adapters.gphome;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.gphome.Menu
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.adapters.common.gridmenu.MenuCallback
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class GPHomeMenuAdapter(
    private val menu: List<Menu>,
    private val menuCallback: MenuCallback,
    private val isDialog:Boolean = false
) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_deen_gphome_menu, parent, false)
        )

    override fun getItemCount(): Int = if(isDialog) menu.size else 4

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun menuVisited(menu: Menu) {
        val index = this.menu.indexOfFirst { it.Id == menu.Id }
        if(index!=-1) {
            this.menu[index].IsVisited = true

            if(!isDialog)
                notifyDataSetChanged()
            else
                notifyItemChanged(index)
        }

    }

    fun getMenuList() = menu

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val menuIcon:AppCompatImageView = itemView.findViewById(R.id.menuIcon)
        private val newBtn:MaterialButton = itemView.findViewById(R.id.newBtn)
        private val moreCount:AppCompatTextView = itemView.findViewById(R.id.moreCount)
        private val menuTitile:AppCompatTextView = itemView.findViewById(R.id.menuTitile)
        private val redDot:View = itemView.findViewById(R.id.redDot)


        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = menu[absoluteAdapterPosition]

            if(absoluteAdapterPosition==3 && !isDialog) {

                menuIcon.load(R.drawable.ic_deen_gp_more_menu)
                menuTitile.text = itemView.context?.getString(R.string.more)

                moreCount.text = "+${menu.size-3}".numberLocale()

                val newFeatureCount = menu.filter { !it.IsVisited }.size
                if(newFeatureCount>0) {
                    newBtn.text =
                        newBtn.context?.getString(R.string.new_icon_count,newFeatureCount.toString().numberLocale())
                    newBtn.show()
                }else
                    newBtn.hide()

                redDot.hide()
                moreCount.show()


            }else{
                menuIcon.imageLoad(
                    placeholder_1_1 = true,
                    url = BASE_CONTENT_URL_SGP + getdata.imageurl1
                )
                menuTitile.text = getdata.ArabicText

                moreCount.hide()

                redDot.visible(!getdata.IsVisited)

                /*if(!getdata.IsVisited) {
                    newBtn.text = newBtn.context.getString(R.string.new_btn_txt)
                    newBtn.show()
                }
                else {
                    newBtn.hide()
                }*/

                newBtn.hide()

            }

            itemView.setOnClickListener {
                if(absoluteAdapterPosition == 3 && !isDialog)
                    menuCallback.showMenuBottomSheetDialog(menu)
                else
                menuCallback.menuClicked(getdata)
            }

        }
    }
}