package com.deenislamic.sdk.views.adapters.qurbani;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QurbaniCallback
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import org.jsoup.Jsoup

internal class QurbaniOnlineHaatAdapter(private val data: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<QurbaniCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_qurbani_online_haat, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val banner:AppCompatImageView = itemView.findViewById(R.id.banner)
        private val logo:ShapeableImageView = itemView.findViewById(R.id.logo)
        private val title:AppCompatTextView = itemView.findViewById(R.id.title)
        private val directionBtn:MaterialButton = itemView.findViewById(R.id.directionBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = data[position]

            banner.imageLoad(url = "$BASE_CONTENT_URL_SGP${getdata.ImageUrl}", placeholder_16_9 = true)
            logo.imageLoad(url = "$BASE_CONTENT_URL_SGP${getdata.Pronunciation}", placeholder_1_1 = true)
            title.text = Jsoup.parse(getdata.Text.toString()).text()
            directionBtn.setOnClickListener {
                callback?.qurbaniHaatDirection(getdata)
            }

        }
    }
}