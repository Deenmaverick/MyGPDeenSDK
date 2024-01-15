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
import com.deenislam.sdk.utils.RECYCLERFOOTER
import com.deenislam.sdk.utils.RECYCLER_DATA_AVAILABLE
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

    when(viewType)
    {
        RECYCLER_DATA_AVAILABLE ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_hadith_collection, parent, false)
        )

        else ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.layout_footer, parent, false)
        )


    }

    fun update(data: List<Data>)
    {
        collectionList.clear()
        collectionList.addAll(data)
        notifyItemChanged(data.size-1)
    }
    override fun getItemCount(): Int = collectionList.size+1

    override fun getItemViewType(position: Int): Int =
        when (collectionList.size) {
            position -> RECYCLERFOOTER
            else -> RECYCLER_DATA_AVAILABLE
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val nameArabic:AppCompatTextView by lazy { itemView.findViewById(R.id.nameArabic) }
        private val nameEn:AppCompatTextView by lazy { itemView.findViewById(R.id.nameEn) }
        private val hadithCount:AppCompatTextView by lazy { itemView.findViewById(R.id.hadithCount) }
        private val ic_hadith_book:AppCompatImageView by lazy { itemView.findViewById(R.id.ic_hadith_book) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                RECYCLER_DATA_AVAILABLE ->
                {
                    val hadith = collectionList[position]

                    ic_hadith_book.imageLoad(
                        url = BASE_CONTENT_URL_SGP+"Hadith/hadithbook/"+hadith.Name+".png",
                        placeholder_1_1 = true
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

    }
}
internal interface HadithCollectionCallback
{
    fun CollectionClicked(name: String, title: String)
}