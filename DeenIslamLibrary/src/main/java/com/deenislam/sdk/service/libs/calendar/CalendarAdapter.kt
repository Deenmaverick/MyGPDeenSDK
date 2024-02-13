package com.deenislam.sdk.service.libs.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.deenislam.sdk.R
import com.google.gson.Gson

internal class CalendarAdapter(private val context: Context, private var days: List<CalendarDay>) : BaseAdapter() {

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): CalendarDay = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val day = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_calendar_day, parent, false)
        val textView = view.findViewById<TextView>(R.id.dayText)

        textView.text = day.day

        val calendar = java.util.Calendar.getInstance()
        // Set the specific year, month, and day for the calendar instance
        calendar.set(day.year, day.month, day.day.toInt())

        val isFriday = calendar.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.FRIDAY

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

        return view
    }




    fun updateDays(newDays: List<CalendarDay>) {
        days = newDays
        Log.e("updateDays",Gson().toJson(newDays))
        notifyDataSetChanged()
    }


}

