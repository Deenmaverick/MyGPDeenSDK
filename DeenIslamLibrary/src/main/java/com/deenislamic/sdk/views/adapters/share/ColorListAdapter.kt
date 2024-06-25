package com.deenislamic.sdk.views.adapters.share;

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.ShareCallback
import com.deenislamic.sdk.service.models.share.ColorList
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.isDeepColor
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class ColorListAdapter(private val textColorArray: ArrayList<ColorList>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var activeIndex = 0
    private val callback = CallBackProvider.get<ShareCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_color_list, parent, false)
        )

    override fun getItemCount(): Int = textColorArray.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val colorBtn:MaterialButton = itemView.findViewById(R.id.colorBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = textColorArray[absoluteAdapterPosition]

            if(activeIndex == absoluteAdapterPosition) {
                colorBtn.icon = ContextCompat.getDrawable(itemView.context, R.drawable.deen_ic_check)
                if(Color.parseColor(getData.code).isDeepColor())
                    colorBtn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.deen_white))
                else
                    colorBtn.iconTint = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.deen_primary))

            }
            else
                colorBtn.icon = null


            colorBtn.backgroundTintList = ColorStateList.valueOf(Color.parseColor(getData.code))

            itemView.setOnClickListener {
                val prevActive = activeIndex
                activeIndex = absoluteAdapterPosition
                if(prevActive != activeIndex)
                    notifyItemChanged(prevActive)
                notifyItemChanged(activeIndex)
                callback?.selectedTextColor(absoluteAdapterPosition,getData)
            }

        }
    }
}