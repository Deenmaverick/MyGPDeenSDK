package com.deenislam.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.views.base.BaseViewHolder

internal class HadithCategoryAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hadith_category, parent, false)
        )

    fun update()
    {

    }

    override fun getItemCount(): Int = 10

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    internal inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)

            if(position==0 || position==1)
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 8.dp
            else
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0

        }
    }
}