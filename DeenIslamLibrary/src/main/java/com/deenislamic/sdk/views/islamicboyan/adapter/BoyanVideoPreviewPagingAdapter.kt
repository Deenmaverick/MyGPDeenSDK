package com.deenislamic.sdk.views.islamicboyan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.boyan.videopreview.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class BoyanVideoPreviewPagingAdapter(
    private val boyanVideoCallBack: BoyanVideoClickCallback
) : RecyclerView.Adapter<BaseViewHolder>()  {

    private val boyanVideoList: ArrayList<Data> = arrayListOf()

    private var isListView = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder  = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quranic_v2, parent, false)
    )

    fun update(data: List<Data>)
    {
        boyanVideoList.clear()
        boyanVideoList.addAll(data)
        notifyDataSetChanged()
    }

    fun updateView(isListView: Boolean) {
        this.isListView = isListView
        //notifyItemRangeChanged(0,itemCount)
    }

    override fun getItemCount(): Int = boyanVideoList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val banner: AppCompatImageView by lazy { itemView.findViewById(R.id.banner) }
        private val textContent: AppCompatTextView = itemView.findViewById(R.id.textContent)
        private val subContent: AppCompatTextView = itemView.findViewById(R.id.subContent)
        private val mainBtn: MaterialButton = itemView.findViewById(R.id.mainBtn)
        private val isPlay: AppCompatImageView = itemView.findViewById(R.id.icPlay)

        override fun onBind(position: Int) {
            super.onBind(position)

            val boyanVideoItem = boyanVideoList[position]

            Log.d("TheVideoDataDataData", "data: "+boyanVideoItem)

            banner.imageLoad(url = BASE_CONTENT_URL_SGP + boyanVideoItem.imageurl1, placeholder_16_9 = true)

            textContent.text = boyanVideoItem.title
            subContent.visibility = View.INVISIBLE
            mainBtn.visibility = View.GONE
            if (isListView == true){
                isPlay.show()
            }
            if (isListView == false){
                isPlay.hide()
            }


            itemView.setOnClickListener {
                boyanVideoCallBack.videoClick(boyanVideoItem.Id, boyanVideoItem.categoryId, boyanVideoItem.scholarId)
            }
        }
    }
}

interface BoyanVideoClickCallback {
    fun videoClick(id: Int, categoryId: Int, scholarId: Int)
}