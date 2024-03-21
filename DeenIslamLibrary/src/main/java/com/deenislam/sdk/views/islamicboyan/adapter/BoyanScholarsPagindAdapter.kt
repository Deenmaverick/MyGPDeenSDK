package com.deenislam.sdk.views.islamicboyan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.boyan.scholarspaging.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class BoyanScholarsPagindAdapter(
    private val callback: BoyanScholarCallback,
    private val type: String
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val scholarList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder  = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_boyan_scholars_full, parent, false)
    )

    fun update(data: List<Data>)
    {
        scholarList.clear()
        scholarList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = scholarList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val headerImg: AppCompatImageView by lazy { itemView.findViewById(R.id.headerImg) }
        private val name: AppCompatTextView = itemView.findViewById(R.id.pageTitle)
        private val boyanNumbers: AppCompatTextView = itemView.findViewById(R.id.txtviwName)

        override fun onBind(position: Int) {
            super.onBind(position)

            val scholarItem = scholarList[position]

            headerImg.imageLoad(url = BASE_CONTENT_URL_SGP + scholarItem.ImageUrl, placeholder_16_9 = true)

            name.text = scholarItem.Name

            if (type == "boyan"){
                boyanNumbers.text = scholarItem.ECount.toString().numberLocale()+" বয়ান"
            } else if (type == "book"){
                boyanNumbers.text = scholarItem.ECount.toString().numberLocale()+" বই"
            }


            itemView.setOnClickListener {
                callback.scholarClick(scholarItem)
            }
        }
    }
}

internal interface BoyanScholarCallback {
    fun scholarClick(data: Data)
}