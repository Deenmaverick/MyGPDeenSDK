package com.deenislamic.sdk.views.adapters.quran.learning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.quran.learning.FaqList
import com.deenislamic.sdk.utils.load
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class QuranClassFaqList(private val faqlist: ArrayList<FaqList>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quran_class_faq_list, parent, false)
        )

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val title:AppCompatTextView by lazy { itemView.findViewById(R.id.title) }
        private val content:AppCompatTextView by lazy { itemView.findViewById(R.id.content) }
        private val ic_right:AppCompatImageView by lazy { itemView.findViewById(R.id.ic_right) }

        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = faqlist[position]

            title.text = getData.question
            content.text = getData.content

            itemView.setOnClickListener {
                content.visible(!content.isVisible)
                if(content.isVisible)
                    ic_right.load(R.drawable.deen_ic_dropdown_expand)
                else
                    ic_right.load(R.drawable.ic_dropdown)
            }

        }
    }
}