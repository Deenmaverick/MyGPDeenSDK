package com.deenislam.sdk.views.adapters.qurbani;

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.AppPreference
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Detail
import com.deenislam.sdk.utils.JustifiedTextView
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.htmlFormat
import com.deenislam.sdk.utils.qurbani.getBanglaSize
import com.deenislam.sdk.views.base.BaseViewHolder
import org.jsoup.Jsoup

internal class QurbaniContentAdapter(private val details: List<Detail>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var contentSetting = AppPreference.getContentSetting()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_qurbani_content, parent, false)
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
        private val title:AppCompatTextView = itemView.findViewById(R.id.title)
        private val arabicText:AppCompatTextView = itemView.findViewById(R.id.arabicText)
        private val content: JustifiedTextView = itemView.findViewById(R.id.content)
        private val referance:JustifiedTextView = itemView.findViewById(R.id.referance)
        private val subText:JustifiedTextView = itemView.findViewById(R.id.subText)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = details[position]

            title.text = getdata.Title
            subText.text = getdata.Text.htmlFormat()
            arabicText.text = Jsoup.parse(getdata.TextInArabic).text()
            content.text = getdata.Pronunciation.htmlFormat()
            referance.text = getdata.reference.htmlFormat()

            title.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(16F)
                )
            }

            subText.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(16F)
                )
            }

            arabicText.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.arabicFontSize.getBanglaSize(24F)
                )
            }

            content.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(16F)
                )
            }

            referance.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(14F)
                )
            }

            when(contentSetting.arabicFont){

                1-> {
                    val customFont =
                        ResourcesCompat.getFont(itemView.context, R.font.indopak)
                    arabicText.typeface = customFont
                }

                2-> {
                    val customFont =
                        ResourcesCompat.getFont(itemView.context, R.font.kfgqpc_font)
                    arabicText.typeface = customFont
                }

                3-> {
                    val customFont =
                        ResourcesCompat.getFont(itemView.context, R.font.al_majed_quranic_font_regular)
                    arabicText.typeface = customFont
                }
            }

            if(getdata.Title.isEmpty()) {
                title.hide()
                subText.setPadding(0,0,0,0)
            }
            if(getdata.Text.isEmpty())
                subText.hide()
            if(getdata.TextInArabic.isEmpty())
                arabicText.hide()
            if(getdata.Pronunciation.isEmpty())
                content.hide()
            if(getdata.reference.isEmpty())
                referance.hide()

        }
    }
}