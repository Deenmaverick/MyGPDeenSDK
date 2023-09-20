package com.deenislam.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.base.BaseViewHolder

internal class SelectSurahAdapter(
    private val surahList: List<Chapter>,
    private val juzList: List<Juz>?,
    private val callback: SelectSurahCallback,
    private val isSurahMode:Boolean
) : RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    var surahFilter : List<Chapter> = surahList
    var juzFilter : List<Juz>? = juzList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_select_surah, parent, false)
        )

    override fun getItemCount(): Int = if(isSurahMode) surahFilter.size else juzFilter?.size?:0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount:AppCompatTextView = itemView.findViewById(R.id.surahCount)
        private val surahName:AppCompatTextView = itemView.findViewById(R.id.surahName)
        private val surahSub:AppCompatTextView = itemView.findViewById(R.id.surahSub)
        private val arbSurah:AppCompatTextView = itemView.findViewById(R.id.arbSurah)
        private val ic_right:AppCompatImageView = itemView.findViewById(R.id.ic_right)

        override fun onBind(position: Int) {
            super.onBind(position)

            if(isSurahMode)
            {
                arbSurah.show()
                ic_right.hide()

                val surahPost = surahFilter[position].id

                surahCount.text = surahFilter[position].id.toString().numberLocale()
                surahName.text =
                    if(DeenSDKCore.GetDeenLanguage() == "bn") (surahFilter[position].id-1).getSurahNameBn()
                    else
                        surahFilter[position].name_simple

                arbSurah.text = "${if(surahPost<10)0 else ""}${if(surahPost<100)0 else ""}${surahPost}"
                arbSurah.text =
                    "${if (surahPost < 10) 0 else ""}${if (surahPost < 100) 0 else ""}${surahPost}"

                surahSub.text = surahSub.context.resources.getString(R.string.quran_popular_surah_ayat,surahFilter[position].translated_name.name+" • ",surahFilter[position].verses_count.toString().numberLocale())

                itemView.setOnClickListener {
                    callback.selectedSurah(surahPost - 1)
                }



            }
            else
            {

                arbSurah.hide()
                ic_right.show()

                val juzData = juzFilter?.get(position)

                surahCount.text = juzData?.juz_number.toString().numberLocale()
                surahName.text =   surahName.context.resources.getString(R.string.quran_para_adapter_title,juzData?.juz_number.toString().numberLocale())

                val verse_mapping = juzData?.verse_mapping?.let { it::class.java.declaredFields }
                var suraSubTxt = ""

                if(surahList.size == 114) {
                    if (verse_mapping != null) {
                        for (surah in verse_mapping) {
                            surah.isAccessible = true

                            val value = surah.get(juzData.verse_mapping)

                            if (value is String && value.isNotEmpty()) {
                                suraSubTxt += if(DeenSDKCore.GetDeenLanguage() == "bn") (surah.name.toInt()-1).getSurahNameBn() +" "
                                else
                                    "${surahList[surah.name.toInt()-1].name_simple} "
                            }
                        }
                    }
                }

                if(suraSubTxt.length>30)
                    suraSubTxt = "${suraSubTxt.substring(0,20)}..."

                surahSub.text = suraSubTxt

                itemView.setOnClickListener {
                    callback.selectedJuz(position)
                }
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) surahFilter = surahList else {
                    val filteredList = ArrayList<Chapter>()
                    surahList
                        .filter {
                            (it.name_simple.lowercase().contains(constraint.toString().lowercase()))

                        }
                        .forEach { filteredList.add(it) }
                    surahFilter = filteredList

                }
                return FilterResults().apply { values = surahFilter }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                surahFilter = if (results?.values == null)
                    ArrayList()
                else
                    results.values as ArrayList<Chapter>

                notifyDataSetChanged()
            }
        }
    }
}

internal interface SelectSurahCallback
{
    fun selectedSurah(position: Int)
    fun selectedJuz(position: Int)
}
