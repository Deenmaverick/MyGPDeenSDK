package com.deenislam.sdk.views.adapters.ramadan;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.ramadan.RamadanDua
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.base.BaseViewHolder

internal class RamadanDuaListAdapter(private val ramadanDua: List<RamadanDua>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_ramadan_dua, parent, false)
        )

    override fun getItemCount(): Int = ramadanDua.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val countTxt:AppCompatTextView = itemView.findViewById(R.id.countTxt)
        private val Title:AppCompatTextView = itemView.findViewById(R.id.Title)
        private val textArabic:AppCompatTextView = itemView.findViewById(R.id.textArabic)
        private val textBangla:AppCompatTextView = itemView.findViewById(R.id.textBangla)
        private val textTranslate:AppCompatTextView = itemView.findViewById(R.id.textTranslate)
        private val banglaLayout:LinearLayout = itemView.findViewById(R.id.banglaLayout)
        private val translateLayout:LinearLayout = itemView.findViewById(R.id.translateLayout)
        private val txtTitleBangla:AppCompatTextView = itemView.findViewById(R.id.txtTitleBangla)
        private val ic_right:AppCompatImageView = itemView.findViewById(R.id.ic_right)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = ramadanDua[absoluteAdapterPosition]

            countTxt.text =
                if(absoluteAdapterPosition+1<10)
                    "0${absoluteAdapterPosition+1}".numberLocale()
                else
                    (absoluteAdapterPosition+1).toString().numberLocale()

            Title.text = getData.Title


            if(getData.TextInArabic.isNotEmpty()) {
                textArabic.text = getData.TextInArabic
            }
            else {
                translateLayout.hide()
                textArabic.hide()
                txtTitleBangla.hide()
            }

            if(getData.Text.isNotEmpty())
                textBangla.text = getData.Text
            else
                banglaLayout.hide()

            if(getData.Pronunciation.isNotEmpty())
            textTranslate.text = getData.Pronunciation
            else
                translateLayout.hide()

            itemView.setOnClickListener {

                textArabic.visible(getData.TextInArabic.isNotEmpty() && !textArabic.isVisible)
                banglaLayout.visible(getData.Text.isNotEmpty() && !banglaLayout.isVisible)
                translateLayout.visible(getData.Pronunciation.isNotEmpty() && !translateLayout.isVisible)

                if(textArabic.isVisible || banglaLayout.isVisible || translateLayout.isVisible)
                    ic_right.load(R.drawable.deen_ic_dropdown_expand)
                else
                    ic_right.load(R.drawable.ic_dropdown)
            }


        }
    }
}