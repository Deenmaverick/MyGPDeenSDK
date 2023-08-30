package com.deenislam.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.SurahCallback
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter
import com.deenislam.sdk.utils.RECYCLERFOOTER
import com.deenislam.sdk.utils.RECYCLER_DATA_AVAILABLE
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.getSurahNameBn
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder

internal class SurahAdapter(
    private val surahCallback: SurahCallback?=null
) : RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    private val surahList: ArrayList<Chapter> = arrayListOf()
    private var surahFilter : List<Chapter> = surahList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        when(viewType)
        {
            RECYCLER_DATA_AVAILABLE ->
                ViewHolder(
                    LayoutInflater.from(parent.context.getLocalContext())
                        .inflate(R.layout.item_quran_popular_surah, parent, false)
                )

            else ->
                ViewHolder(
                    LayoutInflater.from(parent.context.getLocalContext())
                        .inflate(R.layout.layout_footer, parent, false)
                )


        }


    fun update(data: List<Chapter>)
    {
        surahList.clear()
        surahList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (surahFilter.size) {
            position -> RECYCLERFOOTER
            else -> RECYCLER_DATA_AVAILABLE
        }
    override fun getItemCount(): Int = surahFilter.size+1


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount:AppCompatTextView by lazy { itemView.findViewById(R.id.surahCount) }
        private val surahName:AppCompatTextView by lazy { itemView.findViewById(R.id.surahName) }
        private val arbSurah:AppCompatTextView by lazy { itemView.findViewById(R.id.arbSurah) }
        private val surahSub:AppCompatTextView by lazy { itemView.findViewById(R.id.surahSub) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                RECYCLER_DATA_AVAILABLE->
                {
                    val surahPost = position+1

                    surahCount.text = surahFilter[position].id.toString().numberLocale()
                    surahName.text =
                        if(DeenSDKCore.language == "bn") (surahFilter[position].id-1).getSurahNameBn()
                        else
                            surahFilter[position].name_simple

                    arbSurah.text = "${if(surahPost<10)0 else ""}${if(surahPost<100)0 else ""}${surahPost}"
                    surahSub.text = surahSub.context.resources.getString(R.string.quran_popular_surah_ayat,surahFilter[position].translated_name.name+" • ",surahFilter[position].verses_count.toString().numberLocale())

                    itemView.setOnClickListener {
                        surahCallback?.surahClick(surahFilter[position])
                    }
                }
            }
        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                surahFilter = if (charString.isEmpty()) surahList else {
                    val filteredList = ArrayList<Chapter>()
                    surahList
                        .filter {
                            (it.name_simple.lowercase().contains(constraint.toString().lowercase()))

                        }
                        .forEach { filteredList.add(it) }
                    filteredList

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


