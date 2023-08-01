package com.deenislam.sdk.views.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.formateDateTime
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.service.network.response.zakat.Data
import com.deenislam.sdk.utils.dayNameLocale
import com.deenislam.sdk.utils.monthNameLocale
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.timeLocale
import com.google.android.material.button.MaterialButton

internal class ZakatSavedAdapter(
    private val callback: ZakatAdapterCallback
): RecyclerView.Adapter<BaseViewHolder>() {

    private val savedZakatList:ArrayList<Data> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_zakat_saved, parent, false)
        )

    fun update(data: List<Data>)
    {
        savedZakatList.clear()
        savedZakatList.addAll(data)
        notifyDataSetChanged()
    }

    fun delItem(position: Int)
    {
        savedZakatList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = savedZakatList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val datetime:AppCompatTextView = itemView.findViewById(R.id.datetime)
        private val totalAssets:AppCompatTextView = itemView.findViewById(R.id.totalAssets)
        private val payableZakat:AppCompatTextView = itemView.findViewById(R.id.payableZakat)
        private val dateCard:MaterialButton = itemView.findViewById(R.id.dateCard)
        private val delBtn:AppCompatImageView = itemView.findViewById(R.id.delBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            val data = savedZakatList[position]
            val timedate = data.EntryDate.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd MMMM, yyyy • hh:mm aa")

            val dateCardText = data.EntryDate.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd\nMMMM")
            datetime.text = timedate.timeLocale().monthNameLocale()
            totalAssets.text = "৳ ${data.TotalAssets}".numberLocale()
            payableZakat.text = "৳ ${data.ZakatPayable}".numberLocale()
            dateCard.text = dateCardText.timeLocale().monthNameLocale()

            itemView.setOnClickListener {
                callback.viewCalculation(data)
            }

            delBtn.setOnClickListener {
                callback.delHistory(data,position)
            }


        }
    }
}
internal interface ZakatAdapterCallback
{
    fun delHistory(data: Data, position: Int)
    fun viewCalculation(data: Data)
}