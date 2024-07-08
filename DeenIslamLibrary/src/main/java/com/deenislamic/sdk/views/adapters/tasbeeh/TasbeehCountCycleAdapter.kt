package com.deenislamic.sdk.views.adapters.tasbeeh;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.tasbeeh.CountCycle
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.hide
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.show
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView

internal class TasbeehCountCycleAdapter(
    private val countCycleList: ArrayList<CountCycle>,
    private var selectedCount: Int
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var previousSelectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_tasbeeh_count_cycle, parent, false)
        )

    override fun getItemCount(): Int = countCycleList.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun getSelectedCount() = selectedCount

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val countCycleCard:MaterialCardView = itemView.findViewById(R.id.countCycleCard)
        private val title:AppCompatTextView = itemView.findViewById(R.id.title)
        private val icTick:AppCompatImageView = itemView.findViewById(R.id.icTick)
        override fun onBind(position: Int) {
            super.onBind(position)

            val getData = countCycleList[absoluteAdapterPosition]
            
            if(getData.countValue == 0)
                title.text = itemView.context.getString(R.string.unlimited)
            else
                title.text = getData.countValue.toString().numberLocale()


            if(getData.countPos == selectedCount)
            {
                previousSelectedPos = absoluteAdapterPosition
                title.setTextColor(ContextCompat.getColor(countCycleCard.context,R.color.deen_primary))
                icTick.show()
                countCycleCard.strokeColor = ContextCompat.getColor(countCycleCard.context,R.color.deen_primary)
            }
            else
            {
                title.setTextColor(ContextCompat.getColor(countCycleCard.context,R.color.deen_txt_ash))
                icTick.hide()
                countCycleCard.strokeColor = ContextCompat.getColor(countCycleCard.context,R.color.deen_border)

            }

            itemView.setOnClickListener {
                selectedCount = getData.countPos
                if(previousSelectedPos!=-1)
                notifyItemChanged(previousSelectedPos)
                previousSelectedPos = absoluteAdapterPosition
                notifyItemChanged(absoluteAdapterPosition)
            }

        }
    }
}