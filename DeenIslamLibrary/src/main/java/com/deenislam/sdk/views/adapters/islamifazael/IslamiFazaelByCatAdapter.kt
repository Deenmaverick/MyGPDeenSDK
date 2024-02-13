package com.deenislam.sdk.views.adapters.islamifazael;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.IslamiFazaelCallback
import com.deenislam.sdk.service.network.response.islamifazael.bycat.FazaelDataItem
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.hide
import com.deenislam.sdk.utils.show
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

internal class IslamiFazaelByCatAdapter(private val data: List<FazaelDataItem>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<IslamiFazaelCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_quranic_dua, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val container:MaterialCardView = itemView.findViewById(R.id.learnquranBtn1)
        private val duaCat:MaterialButton = itemView.findViewById(R.id.duaCat)
        private val arabicName:AppCompatTextView = itemView.findViewById(R.id.arabicName)
        private val duaTxt:AppCompatTextView = itemView.findViewById(R.id.duaTxt)
        private val duaSub:AppCompatTextView = itemView.findViewById(R.id.duaSub)
        private val surahInfo:AppCompatTextView = itemView.findViewById(R.id.surahInfo)
        private val favBtn:MaterialButton = itemView.findViewById(R.id.favBtn)
        private val favshareBtn:MaterialButton = itemView.findViewById(R.id.favshareBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            favBtn.hide()

            favshareBtn.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val getdata = data[position]

            duaCat.text  = getdata.title

            if(getdata.textinarabic.isNotEmpty())
                arabicName.text = getdata.textinarabic
            else
                arabicName.hide()

            if(getdata.text.isNotEmpty()){
                duaTxt.show()
                duaTxt.text = getdata.text
            }else
                duaTxt.hide()

            if(getdata.reference.isNotEmpty()){
                duaSub.show()
                duaSub.text = getdata.reference
            }else
                duaSub.hide()

            surahInfo.hide()

            if(getdata.IsFavorite)
            {
                favBtn.setIconResource(R.drawable.ic_favorite_primary_active)
                favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.deen_brand_favorite)
                favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.deen_brand_favorite))
                container.setBackgroundResource(R.drawable.deen_brand_error_gradiant)
            }
            else
            {
                favBtn.setIconResource(R.drawable.ic_favorite)
                favBtn.iconTint = AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep)
                favBtn.setTextColor(AppCompatResources.getColorStateList(favBtn.context,R.color.deen_txt_black_deep))
                container.setBackgroundResource(R.color.deen_white)
            }

            favBtn.setOnClickListener {

            }

            favshareBtn.setOnClickListener {
                callback?.FazaelShareClicked(getdata)
            }

        }
    }
}