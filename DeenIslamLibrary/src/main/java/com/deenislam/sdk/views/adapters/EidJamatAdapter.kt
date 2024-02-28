package com.deenislam.sdk.views.adapters;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.EidJamatCallback
import com.deenislam.sdk.service.network.response.eidjamat.EidJamatListResponse
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class EidJamatAdapter(private val results: List<EidJamatListResponse.Data>) :
    RecyclerView.Adapter<BaseViewHolder>() {

    private var callback = CallBackProvider.get<EidJamatCallback>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_eid_jamat, parent, false)
        )

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val mosqueName: AppCompatTextView = itemView.findViewById(R.id.mosqueName)
        private val mosqueLoc: AppCompatTextView = itemView.findViewById(R.id.mosqueLoc)
        //private val shareBtn: MaterialButton = itemView.findViewById(R.id.shareBtn)
        private val locBtn: MaterialButton = itemView.findViewById(R.id.locBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = results[absoluteAdapterPosition]

            mosqueName.text = getData.title
            mosqueLoc.text = getData.text

          /*  shareBtn.setOnClickListener {
                callback = CallBackProvider.get<EidJamatCallback>()
                callback?.mosqueShareClicked(getData)
            }*/

            locBtn.setOnClickListener {
                callback = CallBackProvider.get<EidJamatCallback>()
                callback?.mosqueDirectionClicked(getData)
            }

        }
    }
}