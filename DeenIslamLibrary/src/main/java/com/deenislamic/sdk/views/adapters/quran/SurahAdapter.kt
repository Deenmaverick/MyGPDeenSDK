package com.deenislamic.sdk.views.adapters.quran;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.SurahCallback
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class SurahAdapter : RecyclerView.Adapter<BaseViewHolder>(), Filterable {

    private val surahCallback = CallBackProvider.get<SurahCallback>()

    private var surahList: ArrayList<Data> = arrayListOf()
    private var surahFilter : List<Data> = surahList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =

       /* if(viewType>0 && viewType == itemCount - 1) {
            FooterViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.layout_footer, parent, false)
            )

        }
        else {
            ViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.item_quran_popular_surah, parent, false)
            )
        }*/

        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_quran_popular_surah, parent, false)
        )


    fun update(data: List<Data>) {
        val diffResult = DiffUtil.calculateDiff(
            DataDiffCallback(
                surahList,
                data
            )
        )
        surahList = data as ArrayList<Data>
        surahFilter = surahList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = surahFilter.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

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

            Log.e("SurahAdapter",position.toString())

            val surahPost = position+1

            surahCount.text = surahFilter[position].SurahId.toString().numberLocale()
            surahName.text =
                    /* if(BaseApplication.getLanguage() == "bn") (surahFilter[position].SurahId-1).getSurahNameBn()
                     else*/
                surahFilter[position].SurahName

            arbSurah.text = "${if(surahPost<10)0 else ""}${if(surahPost<100)0 else ""}${surahPost}"
            surahSub.text = surahSub.context.resources.getString(R.string.quran_popular_surah_ayat,surahFilter[position].SurahNameMeaning+" • ",
                surahFilter[position].TotalAyat.numberLocale())

            itemView.setOnClickListener {
                surahCallback?.surahClick(surahFilter[absoluteAdapterPosition])
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                surahFilter = if (charString.isEmpty()) surahList else {
                    val filteredList = ArrayList<Data>()
                    surahList
                        .filter {(
                                it.rKey.lowercase().contains(constraint.toString().lowercase()) ||
                                        it.rKey.lowercase().contains(constraint.toString().lowercase())

                                )
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
                    results.values as ArrayList<Data>

                notifyDataSetChanged()
            }
        }
    }

    internal inner class DataDiffCallback(
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


