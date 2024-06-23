package com.deenislamic.sdk.views.adapters.dailydua;

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.dailydua.todaydua.Data
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.imageLoad
import com.deenislamic.sdk.utils.invisible
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class TodayDuaAdapter(
    private val callback: TodayDuaCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var todayDuaList:ArrayList<Data> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_dua_today, parent, false)
        )

    fun update(data: List<Data>)
    {
        todayDuaList.clear()
        todayDuaList.addAll(data)
        notifyItemInserted(itemCount)
    }

    fun update(position: Int, fav: Boolean)
    {
        todayDuaList[position].IsFavorite = fav
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = todayDuaList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val banner:AppCompatImageView  = itemView.findViewById(R.id.banner)
        private val arabicName:AppCompatTextView = itemView.findViewById(R.id.arabicName)
        private val duaTxt:AppCompatTextView = itemView.findViewById(R.id.duaTxt)
        private val duaSub:AppCompatTextView = itemView.findViewById(R.id.duaSub)
        private val favBtn:MaterialButton = itemView.findViewById(R.id.favBtn)
        private val favshareBtn:MaterialButton = itemView.findViewById(R.id.favshareBtn)
        private val surahInfo:AppCompatTextView = itemView.findViewById(R.id.surahInfo)


        override fun onBind(position: Int) {
            super.onBind(position)

            val  duaData =  todayDuaList[position]
            banner.show()
            //favshareBtn.hide()
            favBtn.hide()

            banner.imageLoad(url = "${duaData.contentBaseUrl}/${duaData.ImageUrl}", placeholder_1_1 = true)

           /* banner.load("${duaData.contentBaseUrl}/${duaData.ImageUrl}")
            {
                //placeholder(R.drawable.placeholder_1_1)
            }*/

            if(duaData.TextInArabic.isEmpty())
            {
                arabicName.hide()
            }
            else {
                arabicName.show()
                arabicName.text = duaData.TextInArabic
            }

            if(duaData.Pronunciation.isEmpty())
                duaTxt.hide()
            else {
                duaTxt.show()
                duaTxt.text = duaData.Pronunciation
            }

            if(duaData.Title.isEmpty())
                duaSub.hide()
            else {
                duaSub.show()
                duaSub.text = duaData.Title
            }

            surahInfo.hide()

            favBtn.setOnClickListener {
                callback.favClick(duaData.IsFavorite,duaData.DuaId,position)
            }

            if(duaData.IsFavorite)
            {
                favBtn.setIconResource(R.drawable.ic_favorite_primary_active)
                favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep)
                favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.deen_primary))

            }
            else
            {
                favBtn.setIconResource(R.drawable.ic_favorite)
                favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep)
                favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep))

            }

            favshareBtn.setOnClickListener {
                banner.doOnLayout {
                    val drawable = banner.drawable
                    if (drawable != null) {
                        val bitmap = drawable.toBitmap()
                        callback.shareDua(bitmap)
                    }
                }
            }

        }
    }
}

internal interface TodayDuaCallback
{
    fun favClick(isFavorite: Boolean, duaId: Int, position: Int)
    fun shareDua(duaImg: Bitmap)
}
