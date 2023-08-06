package com.deenislam.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dailydua.todaydua.Data
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.invisible
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseViewHolder
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
        notifyDataSetChanged()
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
            favshareBtn.hide()
            favBtn.hide()

            banner.load("${duaData.contentBaseUrl}/${duaData.ImageUrl}")
            {
                //placeholder(R.drawable.placeholder_1_1)
            }

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

            duaSub.text = duaData.Title
            surahInfo.invisible()

            favBtn.setOnClickListener {
                callback.favClick(duaData.IsFavorite,duaData.DuaId,position)
            }

            if(duaData.IsFavorite)
            {
                favBtn.setIconResource(R.drawable.ic_favorite_primary_active)
                favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.txt_black_deep)
                favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.primary))

            }
            else
            {
                favBtn.setIconResource(R.drawable.ic_favorite)
                favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.txt_black_deep)
                favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.txt_black_deep))

            }

        }
    }
}

interface TodayDuaCallback
{
    fun favClick(isFavorite: Boolean, duaId: Int, position: Int)
}
