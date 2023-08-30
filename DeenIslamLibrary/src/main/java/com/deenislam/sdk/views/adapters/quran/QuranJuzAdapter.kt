package com.deenislam.sdk.views.adapters.quran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.quran.juz.Juz
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.utils.RECYCLERFOOTER
import com.deenislam.sdk.utils.RECYCLER_DATA_AVAILABLE
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

    when(viewType)
    {
        RECYCLER_DATA_AVAILABLE ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_quran_juz, parent, false)
        )

        else ->
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.layout_footer, parent, false)
        )


    }

    fun update(data: List<Juz>)
    {
        juzList.clear()
        juzList.addAll(data)
        notifyItemInserted(itemCount)
    }

    fun updateSurahList(data: List<Chapter>)
    {
        surahList.clear()
        surahList.addAll(data)
        notifyItemInserted(itemCount)

    }
    override fun getItemCount(): Int = juzList.size+1

    override fun getItemViewType(position: Int): Int =
        when (juzList.size) {
            position -> RECYCLERFOOTER
            else -> RECYCLER_DATA_AVAILABLE
        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount: AppCompatTextView by lazy { itemView.findViewById(R.id.surahCount) }
        private val juz: AppCompatTextView by lazy { itemView.findViewById(R.id.juz) }
        private val surahSub: AppCompatTextView by lazy { itemView.findViewById(R.id.surahSub) }
        /*private val playBtn: AppCompatImageView = itemView.findViewById(R.id.playBtn)
        private val playLoading: CircularProgressIndicator = itemView.findViewById(R.id.playLoading)*/

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                RECYCLER_DATA_AVAILABLE ->
                {
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

                                    if(DeenSDKCore.language == "bn") (surah.name.toInt()-1).getSurahNameBn() +" "
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

    }
}
internal interface JuzCallback
{
    fun juzClicked(juz: Juz)
}