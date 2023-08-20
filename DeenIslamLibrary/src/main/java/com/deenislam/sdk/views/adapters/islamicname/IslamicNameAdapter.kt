package com.deenislam.sdk.views.adapters.islamicname;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.islamicname.Data
import com.deenislam.sdk.views.base.BaseViewHolder

internal class IslamicNameAdapter(
    private val callback: IslamicNameAdapterCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val islamicNameData:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_islamic_name, parent, false)
        )

    fun update(data: List<Data>)
    {
        islamicNameData.clear()
        islamicNameData.addAll(data)
        notifyDataSetChanged()
    }

    fun favUpdate(adapterPosition: Int, bol: Boolean)
    {
        islamicNameData[adapterPosition].IsFavorite = bol
        notifyItemChanged(adapterPosition)
    }

    override fun getItemCount(): Int = islamicNameData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val name: AppCompatTextView = itemView.findViewById(R.id.name)
        private val nameArabic: AppCompatTextView = itemView.findViewById(R.id.nameArabic)
        private val meaning: AppCompatTextView = itemView.findViewById(R.id.meaning)
        private val rightBtn: AppCompatImageView = itemView.findViewById(R.id.rightBtn)


        override fun onBind(position: Int) {
            super.onBind(position)
            val data = islamicNameData[position]
            name.text = data.Name
            nameArabic.text = data.nameinarabic
            meaning.text = data.meaning

            if(data.IsFavorite)
                rightBtn.setImageResource(R.drawable.ic_favorite_primary_active)
            else
                rightBtn.setImageResource(R.drawable.ic_fav_quran)

            rightBtn.setOnClickListener {
                callback.favClick(data,position)
            }
        }
    }
}
internal interface IslamicNameAdapterCallback
{
    fun favClick(data: Data, position: Int)
}
