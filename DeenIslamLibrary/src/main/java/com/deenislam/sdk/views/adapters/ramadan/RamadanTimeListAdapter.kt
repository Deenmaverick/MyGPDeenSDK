package com.deenislam.sdk.views.adapters.ramadan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.ramadan.FastTime
import com.deenislam.sdk.utils.dayNameLocale
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.invisible
import com.deenislam.sdk.utils.monthShortNameLocale
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.views.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class RamadanTimeListAdapter(
    private val fastTime: List<FastTime>,
    private val isRamadan: Boolean = false
) : RecyclerView.Adapter<BaseViewHolder>() {

    private  val HEADER = 0
    private  val ITEMVIEW = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        val layout =  when(viewType)
        {
            HEADER -> R.layout.item_ramadan_time_header
            ITEMVIEW -> R.layout.item_ramadan_time
            else -> throw IllegalArgumentException("Invalid type")
        }

        val view = LayoutInflater
            .from(parent.context.getLocalContext())
            .inflate(layout, parent, false)

        return  ViewHolder(view)

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) HEADER else ITEMVIEW
    }

    override fun getItemCount(): Int = fastTime.size+1

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val date: AppCompatTextView by lazy { itemView.findViewById(R.id.date) }
        private val day:AppCompatTextView by lazy { itemView.findViewById(R.id.day) }
        private val suhoor:AppCompatTextView by lazy { itemView.findViewById(R.id.suhoor) }
        private val iftar:AppCompatTextView by lazy { itemView.findViewById(R.id.iftar) }
        private val border:View by lazy { itemView.findViewById(R.id.border) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                ITEMVIEW ->
                {
                    val currentDate = SimpleDateFormat("dd MMM", Locale.ENGLISH).format(Date())
                    val getdata  = fastTime[position-1]

                    if(!isRamadan) {
                        date.text = getdata.Date.monthShortNameLocale().numberLocale()
                    }
                    else {
                        date.text = getdata.islamicDate
                    }
                        day.text = getdata.Day.dayNameLocale()
                    suhoor.text = "${getdata.Suhoor}".numberLocale()
                    iftar.text = "${getdata.Iftaar}".numberLocale()


                    if((currentDate == getdata.Date && !isRamadan)) {
                        itemView.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.deen_card_bg
                            )
                        )
                        border.invisible()
                    }
                    else if((isRamadan && getdata.isToday)) {
                        itemView.setBackgroundResource(R.drawable.deen_radius_card_bg_color)
                        border.invisible()
                    }
                    else {
                        itemView.setBackgroundResource(0)
                        itemView.setBackgroundColor(0)
                    }

                    if(absoluteAdapterPosition == fastTime.size)
                        border.invisible()
                }

                HEADER ->{

                    if(!isRamadan)
                        date.text = itemView.context.getString(R.string.date)
                    else
                        date.text = itemView.context.getString(R.string.ramadan)

                }
            }
        }
    }
}