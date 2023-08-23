package com.deenislam.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.hadith.preview.Data
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseViewHolder

internal class HadithFavAdapter(
    private val callback: hadithFavCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var hadithDataList:ArrayList<Data> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_dua_favorite, parent, false)
        )

    fun update(data: ArrayList<Data>)
    {
        hadithDataList.addAll(data)
        notifyItemInserted(itemCount)
    }

    fun delItem(position: Int)
    {
        hadithDataList.removeAt(if(position ==1)0 else position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = hadithDataList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val itemTitle:AppCompatTextView = itemView.findViewById(R.id.itemTitle)
        private val duaTxt:AppCompatTextView = itemView.findViewById(R.id.duaTxt)
        private val surahInfo:AppCompatTextView = itemView.findViewById(R.id.surahInfo)
        private val rightBtn: AppCompatImageView = itemView.findViewById(R.id.rightBtn)


        override fun onBind(position: Int) {
            super.onBind(position)

            surahInfo.show()

            itemTitle.text = hadithDataList[position].ChapterName

            if(hadithDataList[position].HadithText.length>120)
                duaTxt.text = "${hadithDataList[position].HadithText.substring(0,120)}..."
            else
                duaTxt.text = hadithDataList[position].HadithText

            surahInfo.text = "—${hadithDataList[position].BookName}"

            rightBtn.setOnClickListener {
                callback.delFav(hadithDataList[position],position)
            }

            itemView.setOnClickListener {

                callback.gotHadithPreview(hadithDataList[position].ChapterNo,hadithDataList[position].BookId,hadithDataList[position].BookName)
            }


        }
    }
}
internal interface hadithFavCallback
{
    fun delFav(data: Data, position: Int)
    fun gotHadithPreview(chapterNo: Int, bookId: Int, bookName: String)
}