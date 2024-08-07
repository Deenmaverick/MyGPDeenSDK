package com.deenislamic.sdk.service.libs.calendar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.RamadanCallback
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import com.google.gson.Gson
import java.util.Calendar

internal class CalendarAdapter(private val context: Context, private var days: List<CalendarDay>) : BaseAdapter() {

    private var callback = CallBackProvider.get<RamadanCallback>()
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): CalendarDay = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val day = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_calendar_day, parent, false)
        val textView = view.findViewById<TextView>(R.id.dayText)

        textView.text = day.day.numberLocale()

        val calendar = Calendar.getInstance()
        // Set the specific year, month, and day for the calendar instance
        calendar.set(day.year, day.month, day.day.toInt())

        val isFriday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

        if (day.isActive) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_white))
            textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_active)
            //textView.setBackgroundColor(ContextCompat.getColor(textView.context,R.color.deen_primary))
        }
        else if (day.isInactive) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_white))
            textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_inactive)
            //textView.setBackgroundColor(ContextCompat.getColor(textView.context,R.color.deen_brand_error))
        }
        else if (day.isNA) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_black_deep))
            textView.setBackgroundResource(0)
        }
        else if (day.isSelected) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_primary))
            textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_selected)
        }
        else if(isFriday && day.monthType == MonthType.CURRENT) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_brand_error))
            textView.setBackgroundResource(0)
        } else {
            when (day.monthType) {
                MonthType.PREVIOUS -> textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_ash))
                MonthType.CURRENT -> textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_black_deep))
                MonthType.NEXT -> {
                   /* if(isFriday) {
                        textView.setTextColor(ContextCompat.getColor(context, R.color.brand_error))
                        textView.setBackgroundResource(0)
                    }
                    else*/
                    textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_ash))
                }
            }
            textView.setBackgroundResource(0)
        }

        view.setOnClickListener {
            callback = CallBackProvider.get<RamadanCallback>()
            if(day.month != currentMonth)
                return@setOnClickListener
            days.firstOrNull { it.isSelected }?.isSelected =false
            day.isSelected = true
            callback?.selectedCalendar(day)
            notifyDataSetChanged()
        }

        return view
    }




    fun updateDays(newDays: List<CalendarDay>) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        days = newDays
        days.firstOrNull{ it.day == day.toString() && it.month == month }?.isSelected = true
        notifyDataSetChanged()
    }


}

