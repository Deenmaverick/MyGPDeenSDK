package com.deenislam.sdk.views.adapters.islamicname;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.islamicname.Data
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.views.base.BaseViewHolder

internal class IslamicNameFavAdapter(
    private val callback: IslamicNameFavAdapterCallback
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val favData:ArrayList<Data> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_islamic_name, parent, false)
        )

    fun update(data: List<Data>)
    {
        favData.clear()
        favData.addAll(data)
        notifyDataSetChanged()
    }


    fun delItem(position: Int)
    {
        favData.removeAt(if(position ==1)0 else position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = favData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val name:AppCompatTextView = itemView.findViewById(R.id.name)
        private val nameArabic:AppCompatTextView = itemView.findViewById(R.id.nameArabic)
        private val meaning:AppCompatTextView = itemView.findViewById(R.id.meaning)
        private val rightBtn:AppCompatImageView = itemView.findViewById(R.id.rightBtn)

        override fun onBind(position: Int) {
            super.onBind(position)

            val data = favData[position]
            name.text = data.Name
            nameArabic.text = data.nameinarabic
            meaning.text = data.meaning

            rightBtn.setOnClickListener {
                callback.delFav(data,position)
            }
        }
    }
}

internal interface IslamicNameFavAdapterCallback
{
    fun delFav(data: Data, position: Int)
}