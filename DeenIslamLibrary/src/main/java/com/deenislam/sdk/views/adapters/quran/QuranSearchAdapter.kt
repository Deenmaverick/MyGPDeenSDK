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
import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislam.sdk.utils.getSurahNameBn
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseViewHolder

internal class SelectSurahAdapter(
    private val surahList: List<Data>,
    private val juzList: List<com.deenislam.sdk.service.network.response.quran.qurangm.paralist.Data>?,
    private val callback: SelectSurahCallback,
    private val isSurahMode:Boolean
) : RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    var surahFilter : List<Data> = surahList
    var juzFilter : List<com.deenislam.sdk.service.network.response.quran.qurangm.paralist.Data>? = juzList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
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

                val surahPost = surahFilter[position].SurahId

                surahCount.text = surahPost.toString().numberLocale()
                surahName.text =
                    if(DeenSDKCore.GetDeenLanguage() == "bn") (surahFilter[position].SurahId-1).getSurahNameBn()
                    else
                        surahFilter[position].SurahName
                surahSub.text = surahSub.context.resources.getString(R.string.quran_popular_surah_ayat,surahFilter[position].SurahNameMeaning+" • ",surahFilter[position].TotalAyat.numberLocale())
                arbSurah.text =
                    "${if (surahPost < 10) 0 else ""}${if (surahPost < 100) 0 else ""}${surahPost}"
                itemView.setOnClickListener {
                    callback.selectedSurah(surahPost - 1)
                }



            }
            else
            {

                arbSurah.hide()
                ic_right.show()

                val juzData = juzFilter?.get(position)

                surahCount.text = juzData?.JuzId.toString().numberLocale()
                surahName.text = surahCount.context.resources.getString(R.string.quran_para_adapter_title,juzData?.JuzId.toString().numberLocale())

                var suraSubTxt = juzData?.JuzName_bn

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
                    val filteredList = ArrayList<Data>()
                    surahList
                        .filter {(
                                it.rKey.lowercase().contains(constraint.toString().lowercase()) ||
                                        it.rKey.lowercase().contains(constraint.toString().lowercase())

                                )
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
                    results.values as ArrayList<Data>

                notifyDataSetChanged()
            }
        }
    }
}

interface SelectSurahCallback
{
    fun selectedSurah(position: Int, byService: Boolean=false)
    fun selectedJuz(position: Int)
}
