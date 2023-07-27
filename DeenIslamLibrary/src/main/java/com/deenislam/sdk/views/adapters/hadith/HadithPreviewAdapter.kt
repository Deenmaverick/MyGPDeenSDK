package com.deenislam.sdk.views.adapters.hadith;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.hadith.preview.HadithPreviewResponse
import com.deenislam.sdk.views.base.BaseViewHolder

const val HEADER:Int = 1
const val CONTENT:Int = 2
internal class HadithPreviewAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val haditDataList: HadithPreviewResponse = HadithPreviewResponse()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =

        when(viewType)
        {
            HEADER -> ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hadith_preview_header, parent, false)
            )

            CONTENT -> ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_hadith_preview, parent, false)
            )

            else -> throw java.lang.IllegalArgumentException("View cannot null")
        }


    fun update(data: HadithPreviewResponse)
    {
        haditDataList.clear()
        haditDataList.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = haditDataList.size+1

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) HEADER
        else CONTENT
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val duaSub:AppCompatTextView by lazy { itemView.findViewById(R.id.duaSub) }
        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                CONTENT ->
                {
                    duaSub.text = haditDataList[position-1].hadithText
                }
            }
        }
    }
}