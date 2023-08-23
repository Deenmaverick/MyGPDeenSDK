package com.deenislam.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.hadith.chapter.Data
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class HadithChapterAdapter(
    private val callback: HadithChapterCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val chapterList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_hadith_chapter_by_collection, parent, false))

    fun update(data: List<Data>)
    {
        chapterList.clear()
        chapterList.addAll(data)
        notifyItemInserted(itemCount)
    }

    override fun getItemCount(): Int = chapterList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val countTxt:AppCompatTextView = itemView.findViewById(R.id.countTxt)
        private val name:AppCompatTextView = itemView.findViewById(R.id.name)
        private val count:AppCompatTextView  = itemView.findViewById(R.id.count)

        override fun onBind(position: Int) {
            super.onBind(position)

            val chapter = chapterList[position]

            countTxt.text = (chapter.ChapterNo).toString()
            name.text = chapter.Name
            count.text = count.context.getString(R.string.hadith_chapter_available_count,chapter.HadithStartNumber.toString().numberLocale(),chapter.HadithEndNumber.toString().numberLocale())

            if(position==0)
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 10.dp
            else
                (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 0

            itemView.setOnClickListener {
                callback.chapterClick(chapter.ChapterNo,chapter.BookId,chapter.Name)
            }

        }
    }
}

internal interface HadithChapterCallback
{
    fun chapterClick(chapterID: Int, bookID: Int, name: String)
}