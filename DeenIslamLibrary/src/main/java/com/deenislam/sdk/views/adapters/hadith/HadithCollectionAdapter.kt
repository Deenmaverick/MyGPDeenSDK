package com.deenislam.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.hadith.Data
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class HadithCollectionAdapter(
    private val callback: HadithCollectionCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val collectionList:ArrayList<Data> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_hadith_collection, parent, false)
        )

    fun update(data: List<Data>)
    {
        collectionList.clear()
        collectionList.addAll(data)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = collectionList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val nameArabic:AppCompatTextView = itemView.findViewById(R.id.nameArabic)
        private val nameEn:AppCompatTextView = itemView.findViewById(R.id.nameEn)
        private val hadithCount:AppCompatTextView = itemView.findViewById(R.id.hadithCount)
        private val ic_hadith_book:AppCompatImageView = itemView.findViewById(R.id.ic_hadith_book)

        override fun onBind(position: Int) {
            super.onBind(position)

            val hadith = collectionList[position]

            ic_hadith_book.imageLoad(
                url = BASE_CONTENT_URL_SGP+"Hadith/hadithbook/"+hadith.Name+".png",
                ic_small = true
            )

            nameArabic.text = hadith.ArabicTitle
            nameEn.text = hadith.Title
            hadithCount.text = hadithCount.context.getString(R.string.hadith_collection_adapter_total,hadith.TotalHadith.toString().numberLocale())

            itemView.setOnClickListener {
                callback.CollectionClicked(hadith.Name,hadith.Title)
            }

            if(position==0)
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 8.dp
            else
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0

        }
    }
}
interface HadithCollectionCallback
{
    fun CollectionClicked(name: String, title: String)
}