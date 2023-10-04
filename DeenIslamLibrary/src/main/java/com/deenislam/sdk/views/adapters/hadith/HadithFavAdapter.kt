package com.deenislam.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
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

    fun update(data: List<Data>)
    {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(hadithDataList, data))
        hadithDataList = data as ArrayList<Data>
        diffResult.dispatchUpdatesTo(this)
       /* hadithDataList.addAll(data)
        notifyItemInserted(itemCount)*/
    }

    fun delItem(position: Int)
    {
        hadithDataList.removeAt(if(position ==1)0 else position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, hadithDataList.size - position)
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

    internal class UserDiffCallback(
        private val oldList: List<Data>,
        private val newList: List<Data>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].Id == newList[newItemPosition].Id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}
internal interface hadithFavCallback
{
    fun delFav(data: Data, position: Int)
    fun gotHadithPreview(chapterNo: Int, bookId: Int, bookName: String)
}
