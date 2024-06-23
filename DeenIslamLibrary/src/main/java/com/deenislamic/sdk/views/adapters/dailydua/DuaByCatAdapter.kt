package com.deenislamic.sdk.views.adapters.dailydua;

import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.network.response.dailydua.duabycategory.Data
import com.deenislamic.sdk.utils.fixArabicComma
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.htmlFormat
import com.deenislamic.sdk.utils.qurbani.getBanglaSize
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.spanApplyArabicNew
import com.deenislamic.sdk.utils.spanApplyReference
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class DuaByCatAdapter(
    private val callback:DuaByCatCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var contentSetting = AppPreference.getContentSetting()

    private val duaList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_dua_content, parent, false)
        )

    fun update(data: List<Data>)
    {
        duaList.clear()
        duaList.addAll(data)
        notifyItemInserted(itemCount)
    }

    fun update(position: Int, fav: Boolean)
    {
        if (position in 0 until duaList.size) {
            // Update the item at the given position
            duaList[position].IsFavorite = fav
            // Notify the adapter that the item at the given position has changed
            notifyItemChanged(position)
        }
    }

    fun update(){
        contentSetting = AppPreference.getContentSetting()
    }


    override fun getItemCount(): Int = duaList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val title: AppCompatTextView = itemView.findViewById(R.id.title)
        private val subText: AppCompatTextView = itemView.findViewById(R.id.subText)
        private val seeBtn:MaterialButton = itemView.findViewById(R.id.seeBtn)
        private val content: ConstraintLayout = itemView.findViewById(R.id.content)
        private val arabicText:AppCompatTextView = itemView.findViewById(R.id.arabicText)
        private val referance:AppCompatTextView = itemView.findViewById(R.id.referance)
        private val mainContent:AppCompatTextView = itemView.findViewById(R.id.mainContent)
        private val bookmarkBtn: AppCompatImageView = itemView.findViewById(R.id.bookmarkBtn)
        private val dotMenu: AppCompatImageView = itemView.findViewById(R.id.dotMenu)

        override fun onBind(position: Int) {
            super.onBind(position)

            val localeContext = itemView.context

            val getdata = duaList[position]

            title.text = getdata.Title
            subText.text = getdata.Text.htmlFormat()

            arabicText.text = getdata.TextInArabic


            referance.text = getdata.Source.htmlFormat()

            val part1 = if(getdata.Transliteration.isNotEmpty())  "${localeContext.getString(R.string.pronunciation_html)}${getdata.Transliteration}".htmlFormat() else ""
            val part2 = if(getdata.Text.isNotEmpty()) "${localeContext.getString(R.string.meaning_html)}${getdata.Text}".htmlFormat() else ""
            val benifit = if(getdata.Benefit.isNotEmpty()) getdata.Benefit.htmlFormat() else ""

            val combined = SpannableStringBuilder()
            if(part1.isNotEmpty()) {
                combined.append(part1)
                combined.append("\n") // Adding extra new lines between parts
            }
            if(part2.isNotEmpty()) {
                combined.append(part2)
                combined.append("\n") // Adding extra new lines between parts
            }
            if(benifit.isNotEmpty()) {
                combined.append(benifit)
            }

            combined.spanApplyArabicNew(itemView.context)
            combined.spanApplyReference()
            mainContent.text = combined

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

            mainContent.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.banglaFontSize.getBanglaSize(16F)
                )
            }

            arabicText.apply {
                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,contentSetting.arabicFontSize.getBanglaSize(24F)
                )
            }

            when(contentSetting.arabicFont){

                1-> {
                    val customFont =
                        ResourcesCompat.getFont(itemView.context, R.font.indopakv2)
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

            arabicText.fixArabicComma()

            if(getdata.IsFavorite)
            {
                bookmarkBtn.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.deen_ic_bookmark_active))
                bookmarkBtn.setColorFilter(ContextCompat.getColor(itemView.context,R.color.deen_primary))

            }
            else
            {
                bookmarkBtn.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.deen_ic_bookmark))
                bookmarkBtn.setColorFilter(ContextCompat.getColor(itemView.context,R.color.deen_txt_black_deep))
            }


            if (getdata.isExpanded) {
                seeBtn.text = seeBtn.context.getString(R.string.see_less)
                seeBtn.icon = ContextCompat.getDrawable(seeBtn.context, R.drawable.deen_ic_dropdown_expand)
                content.show()
                subText.hide()
                dotMenu.show()
                bookmarkBtn.show()
            } else {
                seeBtn.text = seeBtn.context.getString(R.string.details)
                seeBtn.icon = ContextCompat.getDrawable(seeBtn.context, R.drawable.ic_dropdown)
                content.hide()
                subText.show()
                dotMenu.hide()
                bookmarkBtn.hide()
            }

            itemView.setOnClickListener {
                getdata.isExpanded = !getdata.isExpanded
                notifyItemChanged(absoluteAdapterPosition)
                callback.contentClicked(absoluteAdapterPosition,getdata.isExpanded)
            }

            if(getdata.Title.isEmpty()) {
                title.hide()
                subText.setPadding(0,0,0,0)
            }
            if(getdata.Text.isEmpty())
                subText.hide()


            bookmarkBtn.setOnClickListener {
                callback.favDua(getdata.IsFavorite,getdata.DuaId,position)
            }

            dotMenu.setOnClickListener {
                callback.dotMenuClicked(getdata)
            }

        }
    }
}
internal interface DuaByCatCallback
{
    fun favDua(isFavorite: Boolean, duaId: Int, position: Int)
    fun shareDua(enText: String, bnText: String, arText: String,dua: Data)
    fun contentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {

    }
    fun dotMenuClicked(dua: Data)
}
