package com.deenislamic.sdk.views.adapters.prayerlearning;

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.prayerlearning.Rakat
import com.deenislamic.sdk.utils.invisible
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.button.MaterialButton

internal class PrayerRakatAdapter(private val data: ArrayList<Rakat>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_round_color_with_num, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val rakatBtn:MaterialButton = itemView.findViewById(R.id.rakatBtn)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = data[position]

            if(getdata.rakat>0) {
                rakatBtn.text = "${getdata.rakat}".numberLocale()
                rakatBtn.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(rakatBtn.context, getdata.color))
                if (getdata.color == R.color.deen_brand_sky_blue) {
                    rakatBtn.setTextColor(
                        ContextCompat.getColor(
                            rakatBtn.context,
                            R.color.deen_txt_black_deep
                        )
                    )
                }else{
                    rakatBtn.setTextColor(
                        ContextCompat.getColor(
                            rakatBtn.context,
                            R.color.deen_white
                        )
                    )
                }

            }else{
                rakatBtn.invisible()
            }

        }
    }
}