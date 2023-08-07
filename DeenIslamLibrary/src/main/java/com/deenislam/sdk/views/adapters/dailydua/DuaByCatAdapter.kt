package com.deenislam.sdk.views.adapters.dailydua;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.dailydua.duabycategory.Data
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class DuaByCatAdapter(
    private val callback: DuaByCatCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val duaList:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_quranic_dua, parent, false)
        )

    fun update(data: List<Data>)
    {
        duaList.clear()
        duaList.addAll(data)
        notifyDataSetChanged()
    }

    fun update(position: Int, fav: Boolean)
    {
        duaList[position].IsFavorite = fav
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = duaList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val duaCat:MaterialButton = itemView.findViewById(R.id.duaCat)
        private val arabicName:AppCompatTextView = itemView.findViewById(R.id.arabicName)
        private val duaTxt:AppCompatTextView = itemView.findViewById(R.id.duaTxt)
        private val duaSub:AppCompatTextView = itemView.findViewById(R.id.duaSub)
        private val surahInfo:AppCompatTextView = itemView.findViewById(R.id.surahInfo)
        private val favBtn:MaterialButton = itemView.findViewById(R.id.favBtn)
        private val shareBtn:MaterialButton = itemView.findViewById(R.id.favshareBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            surahInfo.hide()
            shareBtn.hide()
            if(duaList.size>0) {
                val dua = duaList[position]
                duaCat.text = "${dua.SubCategoryName}-${position+1}".numberLocale()

                if(dua.TextInArabic.isEmpty())
                    arabicName.hide()
                else {
                    arabicName.show()
                    arabicName.text = dua.TextInArabic
                }

                duaTxt.text = dua.Text
                duaSub.text = dua.Pronunciation

                if(dua.IsFavorite)
                {
                    favBtn.setIconResource(R.drawable.ic_favorite_primary_active)
                    favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.brand_favorite)
                    favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.primary))

                }
                else
                {
                    favBtn.setIconResource(R.drawable.ic_favorite)
                    favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.txt_black_deep)
                    favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.txt_black_deep))

                }

                favBtn.setOnClickListener {
                    callback.favDua(dua.IsFavorite,dua.DuaId,position)
                }

            }
        }
    }
}
internal interface DuaByCatCallback
{
    fun favDua(isFavorite: Boolean, duaId: Int, position: Int)
}
