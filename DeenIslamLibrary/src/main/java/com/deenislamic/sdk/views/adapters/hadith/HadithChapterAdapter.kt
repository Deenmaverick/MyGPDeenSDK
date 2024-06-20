package com.deenislamic.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.hadith.chapter.Data
import com.deenislamic.sdk.utils.RECYCLERFOOTER
import com.deenislamic.sdk.utils.RECYCLER_DATA_AVAILABLE
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class HadithChapterAdapter(
    private val callback: HadithChapterCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val chapterList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =

    when(viewType)
    {
        RECYCLER_DATA_AVAILABLE ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_hadith_chapter_by_collection, parent, false)
        )

        else ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.layout_footer, parent, false)
        )


    }

    fun update(data: List<Data>)
    {
        chapterList.clear()
        chapterList.addAll(data)
        notifyItemInserted(itemCount)
    }

    override fun getItemCount(): Int = chapterList.size+1

    override fun getItemViewType(position: Int): Int =
        when (chapterList.size) {
            position -> RECYCLERFOOTER
            else -> RECYCLER_DATA_AVAILABLE
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val countTxt:AppCompatTextView by lazy { itemView.findViewById(R.id.countTxt) }
        private val name:AppCompatTextView by lazy { itemView.findViewById(R.id.name) }
        private val count:AppCompatTextView  by lazy { itemView.findViewById(R.id.count) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                RECYCLER_DATA_AVAILABLE ->
                {
                    val chapter = chapterList[position]

                    countTxt.text = (chapter.ChapterNo).toString().numberLocale()
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

    }
}

internal interface HadithChapterCallback
{
    fun chapterClick(chapterID: Int, bookID: Int, name: String)
}