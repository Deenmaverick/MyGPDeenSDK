package com.deenislamic.sdk.views.adapters.islamicbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.boyan.categoriespaging.Data
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class IslamicBookCategoryPagingAdapter(
    private val callback: IslamicBookCategoryCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val bookList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
    ViewHolder(
    LayoutInflater.from(parent.context)
    .inflate(R.layout.item_hadith_chapter_by_collection, parent, false)
    )

    fun update(data: List<Data>)
    {
        bookList.clear()
        bookList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int  = bookList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val countTxt: AppCompatTextView = itemView.findViewById(R.id.countTxt)
        private val name: AppCompatTextView = itemView.findViewById(R.id.name)
        private val count: AppCompatTextView = itemView.findViewById(R.id.count)

        override fun onBind(position: Int) {
            super.onBind(position)

            val chapter = bookList[position]

            countTxt.text = (position+1).toString().numberLocale()
            name.text = chapter.category
            count.visibility = View.INVISIBLE

            itemView.setOnClickListener {
                callback.bookCategoryClick(chapter)
            }

        }
    }
}

internal interface IslamicBookCategoryCallback
{
    fun bookCategoryClick(data: Data)
}