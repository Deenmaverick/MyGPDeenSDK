package com.deenislamic.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.AsyncViewStub
import com.deenislamic.sdk.utils.PATCH_COMMON_CARD_LIST
import com.deenislamic.sdk.utils.PATCH_MENU
import com.deenislamic.sdk.utils.PATCH_SINGLE_IMAGE
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.prepareStubView
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.dashboard.patch.DailyDua
import com.deenislamic.sdk.views.dashboard.patch.QuranicItem
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList
import com.deenislamic.sdk.views.hajjandumrah.patch.Menu

internal class AllDuaAdapter(
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val categoryList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        val main_view = LayoutInflater.from(parent.context.getLocalContext())
            .inflate(R.layout.layout_async_match, parent, false)

        val rootview: AsyncViewStub = main_view.findViewById(R.id.widget)


        when (categoryList[viewType].AppDesign) {

            PATCH_MENU ->
            {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            PATCH_COMMON_CARD_LIST -> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                    onBindViewHolder(ViewHolder(main_view,true),viewType)

                }
            }

            PATCH_SINGLE_IMAGE -> {
                prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {
                    onBindViewHolder(ViewHolder(main_view,true),viewType)
                }
            }

            "Dua" -> {
                prepareStubView<View>(rootview, R.layout.dashboard_inc_item_horizontal_list) {
                    onBindViewHolder(ViewHolder(main_view, true), viewType)
                }
            }


            /*  1->
              {
                  prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_quranic_v1) {

                      onBindViewHolder(ViewHolder(main_view,true),viewType)

                  }
              }
              2->
              {
                  prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                      onBindViewHolder(ViewHolder(main_view,true),viewType)

                  }
              }

              3->
              {
                  prepareStubView<View>(rootview.findViewById(R.id.widget),R.layout.layout_horizontal_listview_v2) {

                      onBindViewHolder(ViewHolder(main_view,true),viewType)

                  }
              }*/
        }

        return  ViewHolder(main_view)
    }

    fun update(data: List<com.deenislamic.sdk.service.network.response.dashboard.Data>)
    {
        categoryList.clear()
        categoryList.addAll(data)
        notifyItemInserted(itemCount)
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    override fun getItemCount(): Int = categoryList.size


    inner class ViewHolder(itemView: View,private val loaded:Boolean = false) : BaseViewHolder(itemView) {

        private val menuIcon: AppCompatImageView by lazy { itemView.findViewById(R.id.menuIcon) }
        private val menuTitile:AppCompatTextView by lazy { itemView.findViewById(R.id.menuTitile) }


        /*  private val  catIcon:AppCompatImageView =  itemView.findViewById(R.id.menuIcon)
        private val catName:AppCompatTextView = itemView.findViewById(R.id.menuTitile)
        private val countTv:AppCompatTextView = itemView.findViewById(R.id.countTv)
*/
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            if(loaded) {
                val data = categoryList[position]

                when (data.AppDesign) {

                    PATCH_MENU -> {
                        Menu(itemView,data.Items)
                    }

                    PATCH_COMMON_CARD_LIST -> {
                        //(itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        SingleCardList(
                            itemView,
                            data
                        ).load()
                    }
                    PATCH_SINGLE_IMAGE -> {
                        val helper = QuranicItem(itemView)
                        helper.loadSingleImage(data.Items)
                    }

                    "Dua" -> {
                        DailyDua(itemView, data)
                    }

                }
            }
        }
    }
}
