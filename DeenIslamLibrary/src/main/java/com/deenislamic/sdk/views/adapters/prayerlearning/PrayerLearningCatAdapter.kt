package com.deenislamic.sdk.views.adapters.prayerlearning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.common.HorizontalCardListCallback
import com.deenislamic.sdk.service.network.response.dashboard.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.PATCH_COMMON_CARD_LIST
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.deenislamic.sdk.views.dashboard.patch.SingleCardList
import com.deenislamic.sdk.views.prayerlearning.patch.GridMenuList
import com.deenislamic.sdk.views.prayerlearning.patch.ListMenu
import com.google.android.material.button.MaterialButton

internal class PrayerLearningCatAdapter(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<HorizontalCardListCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        if(viewType == itemCount-1){
            return ViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.layout_footer, parent, false)
            )
        }else {
            return when (data[viewType].AppDesign) {
                "cacl" -> ViewHolder(
                    LayoutInflater.from(parent.context.getLocalContext())
                        .inflate(R.layout.item_zakat_calc_btn, parent, false)
                )

                "subs" -> ViewHolder(
                    LayoutInflater.from(parent.context.getLocalContext())
                        .inflate(R.layout.item_subs_cardview, parent, false)
                )

                "smenu" -> ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_horizontal_listview, parent, false)
                )

                "lmenu" -> ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_horizontal_listview, parent, false)
                )

                PATCH_COMMON_CARD_LIST -> {
                    ViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(R.layout.layout_horizontal_listview_v2, parent, false)
                    )
                }

                else -> throw java.lang.IllegalArgumentException("View cannot null")
            }
        }

    }


    override fun getItemCount(): Int = data.size+1

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val getPremiumTxt: AppCompatTextView by lazy { itemView.findViewById(R.id.getPremiumTxt) }
        private val premiumSub: AppCompatTextView by lazy { itemView.findViewById(R.id.premiumSub) }

        private val calcBtn: MaterialButton by lazy { itemView.findViewById(R.id.newCaculateBtn) }


        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position,viewtype)

            if (viewtype in data.indices) {

                val getdata = data[viewtype]

                when (getdata.AppDesign) {
                    "smenu" -> GridMenuList(itemView, getdata.Items)
                    "lmenu" -> ListMenu(itemView, getdata.Items)
                    "cacl" -> {

                        calcBtn.text = getdata.Items.get(0).ArabicText
                        itemView.setOnClickListener {
                            callback = CallBackProvider.get<HorizontalCardListCallback>()
                            callback?.patchItemClicked(getdata.Items.get(0))
                        }
                    }

                    "subs" -> {

                        getPremiumTxt.text = getdata.Items.get(0)?.ArabicText
                        premiumSub.text = getdata.Items.get(0)?.Text

                        itemView.setOnClickListener {
                            callback = CallBackProvider.get<HorizontalCardListCallback>()
                            callback?.patchItemClicked(getdata.Items.get(0))
                        }
                    }

                    PATCH_COMMON_CARD_LIST -> {
                        //(itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0
                        SingleCardList(
                            view = itemView,
                            data = getdata
                        ).load()
                    }
                }
            }
        }
    }
}