package com.deenislamic.sdk.views.adapters.qurbani;

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.QurbaniCallback
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.network.response.common.subcatcardlist.Data
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.utils.qurbani.getBanglaSize
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson

internal class QurbaniCommonContentAdapter(private val details: List<Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var contentSetting = AppPreference.getContentSetting()
    private val callback = CallBackProvider.get<QurbaniCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_qurbani_common_content, parent, false)
        )

    override fun getItemCount(): Int = details.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun update(){
        contentSetting = AppPreference.getContentSetting()
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val title: AppCompatTextView = itemView.findViewById(R.id.title)
        private val subText: AppCompatTextView = itemView.findViewById(R.id.subText)
        private val seeBtn:MaterialButton = itemView.findViewById(R.id.seeBtn)
        private val contentList:RecyclerView = itemView.findViewById(R.id.contentList)
        private val dotMenu:AppCompatImageView = itemView.findViewById(R.id.dotMenu)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = details[position]

            val getsubtext = getdata.details?.firstOrNull()
            val subtext = if(getsubtext?.Text?.isNotEmpty() == true) getsubtext.Text else getsubtext?.Pronunciation
            Log.e("QurbaniOtherContent",Gson().toJson(subtext))
            title.text = getdata.Title
            subText.text = subtext?.htmlFormat()

            title.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(18F)
                )
            }

            subText.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(16F)
                )
            }


            contentList.apply {
                adapter = getdata.details?.let { QurbaniContentAdapter(it) }
            }

            if (getdata.isExpanded) {
                seeBtn.text = seeBtn.context.getString(R.string.see_less)
                seeBtn.icon = ContextCompat.getDrawable(seeBtn.context, R.drawable.deen_ic_dropdown_expand)
                contentList.show()
                subText.hide()
            } else {
                seeBtn.text = seeBtn.context.getString(R.string.details)
                seeBtn.icon = ContextCompat.getDrawable(seeBtn.context, R.drawable.ic_dropdown)
                contentList.hide()
                subText.show()
            }

            seeBtn.setOnClickListener {
                getdata.isExpanded = !getdata.isExpanded
                notifyItemChanged(absoluteAdapterPosition)
                callback?.qurbaniCommonContentClicked(absoluteAdapterPosition,getdata.isExpanded)
            }

            if(getdata.Title.toString().isEmpty()) {
                title.hide()
                subText.setPadding(0,0,0,0)
            }
            if(subtext?.isEmpty() == true)
                subText.hide()


            dotMenu.setOnClickListener {
                callback?.menu3dotClicked(getdata)
            }

        }
    }
}