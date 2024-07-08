package com.deenislamic.sdk.views.adapters.tasbeeh;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.Tasbeeh
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class SelectDhikirAdapter(private val duaList: ArrayList<Tasbeeh>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_dhikir, parent, false)
        )

    override fun getItemCount(): Int = duaList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val duaArabic:AppCompatTextView = itemView.findViewById(R.id.duaArabic)
        private val duaTxt:AppCompatTextView = itemView.findViewById(R.id.duaTxt)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = duaList[absoluteAdapterPosition]

            duaArabic.text = getdata.dua_arb
            duaTxt.text = if(DeenSDKCore.GetDeenLanguage()  == "bn") getdata.dua_bn else getdata.dua

        }
    }
}