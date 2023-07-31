package com.deenislam.sdk.views.adapters.quran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.Deen
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.getSurahNameBn
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.progressindicator.CircularProgressIndicator

internal class QuranJuzAdapter(
    private val callback: JuzCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val juzList:ArrayList<Juz> = arrayListOf()
    private var surahList: ArrayList<Chapter> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_quran_juz, parent, false)
        )

    fun update(data: List<Juz>)
    {
        juzList.clear()
        juzList.addAll(data)
        notifyDataSetChanged()
    }

    fun updateSurahList(data: List<Chapter>)
    {
        surahList.clear()
        surahList.addAll(data)
        notifyDataSetChanged()

    }
    override fun getItemCount(): Int = juzList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount: AppCompatTextView = itemView.findViewById(R.id.surahCount)
        private val juz: AppCompatTextView = itemView.findViewById(R.id.juz)
        private val surahSub: AppCompatTextView = itemView.findViewById(R.id.surahSub)
        private val playBtn: AppCompatImageView = itemView.findViewById(R.id.playBtn)
        private val playLoading: CircularProgressIndicator = itemView.findViewById(R.id.playLoading)
        override fun onBind(position: Int) {
            super.onBind(position)

            surahCount.text = juzList[position].juz_number.toString().numberLocale()
            juz.text = juz.context.resources.getString(R.string.quran_para_adapter_title,juzList[position].juz_number.toString().numberLocale())

            val verse_mapping = juzList[position].verse_mapping::class.java.declaredFields
            var suraSubTxt = ""

            if(surahList.size == 114) {
                for (surah in verse_mapping) {
                    surah.isAccessible = true

                    val value = surah.get(juzList[position].verse_mapping)

                    if (value is String && value.isNotEmpty()) {
                        suraSubTxt +=

                            if(Deen.language == "bn") (surah.name.toInt()-1).getSurahNameBn() +" "
                            else
                                "${surahList[surah.name.toInt()-1].name_simple} "


                    }
                }
            }

            if(suraSubTxt.length>30)
                suraSubTxt = "${suraSubTxt.substring(0,30)}..."

            surahSub.text = suraSubTxt

            itemView.setOnClickListener {
                callback.juzClicked(juzList[position])
            }

        }


    }
}
interface JuzCallback
{
    fun juzClicked(juz: Juz)
}