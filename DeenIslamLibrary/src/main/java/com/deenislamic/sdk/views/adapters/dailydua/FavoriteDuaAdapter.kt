package com.deenislamic.sdk.views.adapters.dailydua;

import android.text.SpannableStringBuilder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.AppPreference
import com.deenislamic.sdk.service.network.response.dailydua.favdua.Data
import com.deenislamic.sdk.utils.fixArabicComma
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.htmlFormat
import com.deenislamic.sdk.utils.qurbani.getBanglaSize
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.utils.spanApplyArabicNew
import com.deenislamic.sdk.utils.spanApplyReference
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class FavoriteDuaAdapter(
    private val callback: FavDuaAdapterCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var contentSetting = AppPreference.getContentSetting()

    private val favList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_daily_dua_favorite, parent, false)
        )

    fun update(data: List<Data>)
    {
        favList.clear()
        favList.addAll(data)
        notifyDataSetChanged()
    }

    fun delItem(position: Int)
    {
        favList.removeAt(if(position ==1)0 else position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, favList.size - position)

    }

    fun update(){
        contentSetting = AppPreference.getContentSetting()
    }

    override fun getItemCount(): Int = favList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val title: AppCompatTextView = itemView.findViewById(R.id.title)
        private val subText: AppCompatTextView = itemView.findViewById(R.id.subText)
        private val seeBtn: MaterialButton = itemView.findViewById(R.id.seeBtn)
        private val content: ConstraintLayout = itemView.findViewById(R.id.content)
        private val arabicText:AppCompatTextView = itemView.findViewById(R.id.arabicText)
        private val referance:AppCompatTextView = itemView.findViewById(R.id.referance)
        private val mainContent:AppCompatTextView = itemView.findViewById(R.id.mainContent)
        private val bookmarkBtn: AppCompatImageView = itemView.findViewById(R.id.bookmarkBtn)
        private val dotMenu: AppCompatImageView = itemView.findViewById(R.id.dotMenu)

        override fun onBind(position: Int) {
            super.onBind(position)

           /* itemTitle.text = favList[position].SubCategoryName
            duaTxt.text = favList[position].Title

            rightBtn.setOnClickListener {
                callback.favClick(favList[position].DuaId,position)
            }

            itemView.setOnClickListener {
                callback.duaClick(favList[position].DuaId,favList[position].SubCategory,favList[position].SubCategoryName)
            }*/

            val localeContext = itemView.context

            val getdata = favList[position]

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

              /*  2-> {
                    val customFont =
                        ResourcesCompat.getFont(itemView.context, R.font.kfgqpc_font)
                    arabicText.typeface = customFont
                }

                3-> {
                    val customFont =
                        ResourcesCompat.getFont(itemView.context, R.font.al_majed_quranic_font_regular)
                    arabicText.typeface = customFont
                }*/
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
                callback.favClick(favList[position].DuaId,position)
            }

            dotMenu.setOnClickListener {
                callback.dotMenuClicked(getdata)
            }

        }
    }
}
internal interface FavDuaAdapterCallback
{
    fun favClick(duaID: Int, position: Int)
    fun duaClick(duaId: Int, subCategory: Int, category: String)
    fun dotMenuClicked(dua: Data)

    fun contentClicked(absoluteAdapterPosition: Int, isExpanded: Boolean) {

    }
}
