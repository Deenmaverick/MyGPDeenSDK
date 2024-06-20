package com.deenislamic.sdk.views.adapters.islamifazael;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.IslamiFazaelCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class IslamiFazaelListAdapter(private val data: List<com.deenislamic.sdk.service.network.response.islamifazael.Data>) : RecyclerView.Adapter<BaseViewHolder>() {

    private val callback = CallBackProvider.get<IslamiFazaelCallback>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_hadith_chapter_by_collection, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val countTxt:AppCompatTextView = itemView.findViewById(R.id.countTxt)
        private val name:AppCompatTextView = itemView.findViewById(R.id.name)
        private val count:AppCompatTextView = itemView.findViewById(R.id.count)

        override fun onBind(position: Int) {
            super.onBind(position)

            val getdata = data[position]

            countTxt.text = (absoluteAdapterPosition+1).toString().numberLocale()

            name.text = getdata.category
            count.hide()

            itemView.setOnClickListener {
                callback?.FazaelCatClicked(getdata)
            }

        }
    }
}