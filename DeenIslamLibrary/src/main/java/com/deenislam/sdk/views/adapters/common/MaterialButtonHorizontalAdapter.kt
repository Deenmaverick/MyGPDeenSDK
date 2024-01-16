package com.deenislam.sdk.views.adapters.common;

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.common.MaterialButtonHorizontalListCallback
import com.deenislam.sdk.service.network.response.prayerlearning.visualization.Head
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class MaterialButtonHorizontalAdapter(
    private val head: List<Head>,
    private val activeTextColor:Int = R.color.deen_primary,
    private val activeBgColor:Int = R.color.deen_card_bg
    ) : RecyclerView.Adapter<BaseViewHolder>() {

    private var activeIndex = 0
    private val callback = CallBackProvider.get<MaterialButtonHorizontalListCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prayer_learning_header, parent, false)
        )

    override fun getItemCount(): Int = head.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun nextPrev(activeIndex:Int)
    {
        this.activeIndex = activeIndex
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val heading:MaterialButton = itemView.findViewById(R.id.heading)
        override fun onBind(position: Int) {
            super.onBind(position)

            if(activeIndex == absoluteAdapterPosition) {
                heading.setBackgroundColor(ContextCompat.getColor(heading.context,activeBgColor))
                heading.strokeWidth = 0.dp
                heading.setTextColor(ContextCompat.getColor(heading.context,activeTextColor))
            }
            else {
                heading.setBackgroundColor(ContextCompat.getColor(heading.context,R.color.deen_background))
                heading.strokeWidth = 1.dp
                heading.setTextColor(ContextCompat.getColor(heading.context,R.color.deen_txt_ash))
            }
            heading.text = head[absoluteAdapterPosition].Title

            itemView.setOnClickListener {
                if(absoluteAdapterPosition == activeIndex)
                    return@setOnClickListener

                val prevActive = activeIndex
                activeIndex = absoluteAdapterPosition
                if(prevActive != activeIndex)
                notifyItemChanged(prevActive)
                notifyItemChanged(activeIndex)
                callback?.materialButtonHorizontalListClicked(absoluteAdapterPosition)
            }

        }
    }
}