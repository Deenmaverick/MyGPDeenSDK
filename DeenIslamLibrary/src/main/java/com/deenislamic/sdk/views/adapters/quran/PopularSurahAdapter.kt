package com.deenislamic.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.SurahCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.transformPatchToSurahData
import com.deenislamic.sdk.views.base.BaseViewHolder


private const val NO_DATA = 0
private const val DATA_AVAILABLE = 1
private const val FOOTER = 2
internal class PopularSurahAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private var surahList: ArrayList<Item> = arrayListOf()
    private val callback = CallBackProvider.get<SurahCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quran_popular_surah, parent, false)
        )

    fun update(data: List<Item>)
    {
        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                surahList,
                data
            )
        )
        surahList = data as ArrayList<Item>
        diffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int = surahList.size


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount:AppCompatTextView by lazy { itemView.findViewById(R.id.surahCount) }
        private val surahName:AppCompatTextView by lazy { itemView.findViewById(R.id.surahName) }
        private val surahSub:AppCompatTextView by lazy { itemView.findViewById(R.id.surahSub) }
        private val arbSurah:AppCompatTextView by lazy { itemView.findViewById(R.id.arbSurah) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position)
            val surahPost = position+1


            surahCount.text = surahList[position].SurahId.toString().numberLocale()
            surahName.text =
                    /*  if(BaseApplication.getLanguage() == "bn") (surahList[position].SurahId-1).getSurahNameBn()
                      else*/
                surahList[position].MText

            arbSurah.text = "${if(surahPost<10)0 else ""}${if(surahPost<100)0 else ""}${surahPost}"
            surahSub.text = surahSub.context.resources.getString(R.string.quran_popular_surah_ayat,surahList[position].Meaning+" • ",
                surahList[position].ECount.toString().numberLocale())

            itemView.setOnClickListener {
                callback?.surahClick(transformPatchToSurahData(surahList[position]))
            }
        }
    }

    internal inner class DataDiffCallback(
        private val oldList: List<Item>,
        private val newList: List<Item>
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
