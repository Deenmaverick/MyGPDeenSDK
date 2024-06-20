package com.deenislamic.sdk.views.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.quran.QuranOfflineDownloadCallback
import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class OfflineDownloadListAdapter(private val surahDataList: MutableList<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback  = CallBackProvider.get<QuranOfflineDownloadCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quran_offline_download, parent, false)
        )

    fun removeItem(position: Int) {
        surahDataList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int = surahDataList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val surahName:AppCompatTextView = itemView.findViewById(R.id.surahName)
        private val totalAyat:AppCompatTextView = itemView.findViewById(R.id.totalAyat)
        private val icPlayPause:AppCompatImageView = itemView.findViewById(R.id.icPlayPause)
        private val icDelete:AppCompatImageView = itemView.findViewById(R.id.icDelete)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = surahDataList[absoluteAdapterPosition]

            surahName.text = getData.SurahName
            totalAyat.text = totalAyat.context.resources.getString(R.string.quran_popular_surah_ayat,getData.SurahNameMeaning+" • ",
                getData.TotalAyat.numberLocale())

            icPlayPause.setOnClickListener {
                callback?.playBtnClicked(getData)
            }

            icDelete.setOnClickListener {
                callback?.deleteOfflineQuran(getData.folderLocation,absoluteAdapterPosition)
            }
        }
    }
}