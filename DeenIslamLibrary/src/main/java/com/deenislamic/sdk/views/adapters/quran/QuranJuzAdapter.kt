package com.deenislamic.sdk.views.adapters.quran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.quran.qurangm.paralist.Data
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.adapters.common.FooterViewHolder
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.progressindicator.CircularProgressIndicator

internal class QuranJuzAdapter() : RecyclerView.Adapter<BaseViewHolder>() {

    private var juzList:List<Data> = arrayListOf()
    private val callback = CallBackProvider.get<JuzCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =

        if(viewType>0 && viewType == itemCount - 1) {
            FooterViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.layout_footer, parent, false)
            )

        }
    else {
            ViewHolder(
                LayoutInflater.from(parent.context.getLocalContext())
                    .inflate(R.layout.item_quran_juz, parent, false)
            )
        }

    fun update(data: List<Data>)
    {
        juzList = data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = juzList.size+if(juzList.isNotEmpty())1 else 0

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val surahCount:AppCompatTextView = itemView.findViewById(R.id.surahCount)
        private val juz:AppCompatTextView = itemView.findViewById(R.id.juz)
        private val surahSub:AppCompatTextView = itemView.findViewById(R.id.surahSub)
        private val playBtn: AppCompatImageView = itemView.findViewById(R.id.playBtn)
        private val playLoading: CircularProgressIndicator = itemView.findViewById(R.id.playLoading)
        override fun onBind(position: Int) {
            super.onBind(position)

            surahCount.text = juzList[position].JuzId.toString().numberLocale()
            juz.text = juz.context.resources.getString(R.string.quran_para_adapter_title,juzList[position].JuzId.toString().numberLocale())

            var suraSubTxt = if(DeenSDKCore.GetDeenLanguage() == "en")
                juzList[position].JuzName_en
            else
                juzList[position].JuzName_bn


            if(suraSubTxt.length>30)
                suraSubTxt = "${suraSubTxt.substring(0,30)}..."

            surahSub.text = suraSubTxt

            itemView.setOnClickListener {
                callback?.juzClicked(juzList[absoluteAdapterPosition])
            }
        }
    }
    internal inner class DataDiffCallback(
        private val oldList: List<com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data>,
        private val newList: List<com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data>
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
internal interface JuzCallback
{
    fun juzClicked(juz: Data)
}