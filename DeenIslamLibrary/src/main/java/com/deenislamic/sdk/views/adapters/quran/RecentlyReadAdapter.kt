package com.deenislamic.sdk.views.adapters.quran;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.SurahCallback
import com.deenislamic.sdk.service.network.response.dashboard.Item
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.transformPatchToSurahData
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.progressindicator.LinearProgressIndicator

internal class RecentlyReadAdapter(private val items: List<Item>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<SurahCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quran_recent_read, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val surahName: AppCompatTextView by lazy { itemView.findViewById(R.id.appCompatTextView1) }
        private val surahNameArabic: AppCompatTextView by lazy { itemView.findViewById(R.id.surahName) }
        private val progress: LinearProgressIndicator by lazy { itemView.findViewById(R.id.progress) }
        private val totalAyath: AppCompatTextView by lazy { itemView.findViewById(R.id.appCompatTextView2) }


        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = items[position]

            surahName.text = "${getData.SurahId}. ${getData.MText}".numberLocale()
            surahNameArabic.text =  "${if(getData.SurahId<10)0 else ""}${if(getData.SurahId<100)0 else ""}${getData.SurahId}"
            progress.hide()
            totalAyath.text = totalAyath.context.getString(R.string.recent_read_total_ayat,
                getData.ECount.numberLocale()
            )

            itemView.setOnClickListener {
                callback = CallBackProvider.get<SurahCallback>()
                callback?.surahClick(transformPatchToSurahData(items[absoluteAdapterPosition]))
            }

        }
    }

}
