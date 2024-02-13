package com.deenislam.sdk.views.adapters.quran.learning;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.utils.BASE_CONTENT_URL_SGP
import com.deenislam.sdk.utils.imageLoad
import com.deenislam.sdk.views.base.BaseViewHolder
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.ContenTeacher

internal class QuranUstadList(private val contenTeacher: ContenTeacher) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ustad_list, parent, false)
        )

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val icProfile:AppCompatImageView = itemView.findViewById(R.id.icProfile)
        private val title:AppCompatTextView = itemView.findViewById(R.id.title)
        private val subTitle:AppCompatTextView by lazy { itemView.findViewById(R.id.subTitle) }
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = contenTeacher

            icProfile.imageLoad(url = BASE_CONTENT_URL_SGP+getData.TeacherImageUrl, placeholder_1_1 = true)
            title.text = getData.TeacherName
            subTitle.text = getData.TeacherIntro


        }
    }
}