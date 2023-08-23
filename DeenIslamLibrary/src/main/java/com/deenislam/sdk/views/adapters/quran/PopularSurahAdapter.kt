package com.deenislam.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.SurahCallback
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.getSurahNameBn
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder


private const val NO_DATA = 0
private const val DATA_AVAILABLE = 1
internal class PopularSurahAdapter(
    private val callback: SurahCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val surahList: ArrayList<Chapter> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        if(viewType == DATA_AVAILABLE)
            ViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.item_quran_popular_surah, parent, false)
            )
        else
            ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_no_data, parent, false)
            )

    fun update(data: List<Chapter>)
    {
        surahList.clear()
        surahList.addAll(data)
        notifyItemInserted(itemCount)
    }

    override fun getItemViewType(position: Int): Int =
        if(surahList.size==0) NO_DATA else DATA_AVAILABLE

    override fun getItemCount(): Int = if(surahList.size==0) 1 else surahList.size


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount: AppCompatTextView by lazy { itemView.findViewById(R.id.surahCount) }
        private val surahName: AppCompatTextView by lazy { itemView.findViewById(R.id.surahName) }
        private val surahSub: AppCompatTextView by lazy { itemView.findViewById(R.id.surahSub) }
        private val arbSurah: AppCompatTextView by lazy { itemView.findViewById(R.id.arbSurah) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)
            when(viewtype)
            {
                DATA_AVAILABLE->
                {
                    val surahPost = position+1


                    surahCount.text = surahList[position].id.toString().numberLocale()
                    surahName.text =
                        if(DeenSDKCore.language == "bn") (surahList[position].id-1).getSurahNameBn()
                    else
                        surahList[position].name_simple
                    
                    arbSurah.text = "${if(surahPost<10)0 else ""}${if(surahPost<100)0 else ""}${surahPost}"
                    surahSub.text = surahSub.context.resources.getString(R.string.quran_popular_surah_ayat,surahList[position].translated_name.name+" • ",surahList[position].verses_count.toString().numberLocale())

                    itemView.setOnClickListener {
                        callback.surahClick(surahList[position])
                    }
                }
            }
        }

    }
}
