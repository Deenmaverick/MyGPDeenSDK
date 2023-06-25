package com.deenislam.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.quran.SurahListData
import com.deenislam.sdk.views.base.BaseViewHolder

internal class SurahAdapter(
    private val surahCallback: SurahCallback?=null
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val surahList: ArrayList<SurahListData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quran_popular_surah, parent, false)
        )

    fun update(data: List<SurahListData>)
    {
        surahList.clear()
        surahList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = surahList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount:AppCompatTextView by lazy { itemView.findViewById(R.id.surahCount) }
        private val surahName:AppCompatTextView by lazy { itemView.findViewById(R.id.surahName) }
        private val arbSurah:AppCompatTextView by lazy { itemView.findViewById(R.id.arbSurah) }
        private val surahSub:AppCompatTextView by lazy { itemView.findViewById(R.id.surahSub) }

        override fun onBind(position: Int) {
            super.onBind(position)

            val surahPost = position+1

            surahCount.text = surahList[position].SurahNo.toString()
            surahName.text = surahList[position].Name
            arbSurah.text = "${if(surahPost<10)0 else ""}${if(surahPost<100)0 else ""}${surahPost}"
            surahSub.text = "${surahList[position].NameMeaning} • ${surahList[position].TotalAyat} Ayahs"

            itemView.setOnClickListener {
                surahCallback?.surahClick(surahList[position])
            }

        }
    }
}

internal interface SurahCallback
{
    fun surahClick(surahListData: SurahListData)
}


