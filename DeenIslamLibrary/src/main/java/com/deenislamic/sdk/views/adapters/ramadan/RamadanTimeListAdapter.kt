package com.deenislamic.sdk.views.adapters.ramadan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.ramadan.FastTime
import com.deenislamic.sdk.utils.dayNameLocale
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.invisible
import com.deenislamic.sdk.utils.monthShortNameLocale
import com.deenislamic.sdk.utils.numberLocale
import com.deenislamic.sdk.utils.tryCatch
import com.deenislamic.sdk.views.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class RamadanTimeListAdapter(
    private val fastTime: List<FastTime>,
    private val isRamadan: Boolean = false
) : RecyclerView.Adapter<BaseViewHolder>() {

    private  val HEADER = 0
    private  val ITEMVIEW = 1

    private var displayedData = fastTime.take(10).toMutableList()
    private var isShowMoreMenu = false
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

    override fun getItemCount(): Int = displayedData.size+1


    fun seeMoreMenu()
    {
        isShowMoreMenu = !isShowMoreMenu
        displayedData = if (isShowMoreMenu) fastTime.toMutableList() else fastTime.take(10).toMutableList()
        notifyDataSetChanged()

    }

    fun isSeeMoreMenu() = isShowMoreMenu

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position,getItemViewType(position))
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val arabicDate:AppCompatTextView by lazy { itemView.findViewById(R.id.arabicDate) }
        private val engDate:AppCompatTextView by lazy { itemView.findViewById(R.id.engDate) }
        private val suhoor:AppCompatTextView by lazy { itemView.findViewById(R.id.suhoor) }
        private val iftar:AppCompatTextView by lazy { itemView.findViewById(R.id.iftar) }
        private val border:View by lazy { itemView.findViewById(R.id.border) }
        private val date:AppCompatTextView by lazy { itemView.findViewById(R.id.date) }

        override fun onBind(position: Int, viewtype: Int) {
            super.onBind(position, viewtype)

            when(viewtype)
            {
                ITEMVIEW ->
                {
                    val currentDate = SimpleDateFormat("dd MMM", Locale.ENGLISH).format(Date())
                    val getdata  = fastTime[position-1]


                    tryCatch {
                        arabicDate.text = "${getdata.islamicDate.split(",")[0]}".numberLocale().dayNameLocale()
                    }

                    engDate.text = "${getdata.Day.dayNameLocale()}, ${getdata.Date}".dayNameLocale().monthShortNameLocale().numberLocale()
                    /*if(!isRamadan) {
                        date.text = getdata.Date.monthShortNameLocale().numberLocale()
                    }
                    else {
                        date.text = getdata.islamicDate
                    }*/
                    //day.text = getdata.Day.dayNameLocale()
                    suhoor.text = "${getdata.Suhoor}".numberLocale()
                    iftar.text = "${getdata.Iftaar}".numberLocale()


                    if((currentDate == getdata.Date && !isRamadan)) {
                        /*itemView.setBackgroundColor(
                            ContextCompat.getColor(
                                itemView.context,
                                R.color.card_bg
                            )
                        )*/
                        itemView.setBackgroundResource(R.drawable.deen_card_bg_radius)
                        border.invisible()
                    }
                    else if((isRamadan && getdata.isToday)) {
                        itemView.setBackgroundResource(R.drawable.deen_card_bg_radius)
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