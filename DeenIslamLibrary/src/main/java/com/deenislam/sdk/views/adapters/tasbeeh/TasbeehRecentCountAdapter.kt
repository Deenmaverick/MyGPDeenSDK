package com.deenislam.sdk.views.adapters.tasbeeh;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.Tasbeeh
import com.deenislam.sdk.views.base.BaseViewHolder

class TasbeehRecentCountAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    private val tasbeehData:ArrayList<Tasbeeh> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tasbeeh_recent_count, parent, false)
        )

    fun update(data: List<Tasbeeh>)
    {
        tasbeehData.clear()
        tasbeehData.addAll(data)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = tasbeehData.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val  listPos:AppCompatTextView by lazy { itemView.findViewById(R.id.listPos) }
        private val duaName:AppCompatTextView by lazy { itemView.findViewById(R.id.duaName) }
        private val duaCount:AppCompatTextView by lazy { itemView.findViewById(R.id.duaCount) }
        override fun onBind(position: Int) {
            super.onBind(position)
            listPos.text = "0"+(position+1).toString()
            duaName.text = tasbeehData[position].dua
            duaCount.text =  if(tasbeehData[position].dua_count>1)
                "${tasbeehData[position].dua_count} times"
            else
                "${tasbeehData[position].dua_count} time"

        }
    }
}
