package com.deenislamic.sdk.service.libs.hijricalendar

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import com.deenislamic.sdk.R
import com.deenislamic.sdk.utils.numberLocale
import java.util.Calendar

internal class HijriCalendarAdapter(private val context: Context, private var days: List<CalendarDay>) : BaseAdapter() {

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): CalendarDay = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val day = getItem(position) as CalendarDay

        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_hijri_calendar_day, parent, false)
        val textView = view.findViewById<TextView>(R.id.dayText)
        val hijriTextview = view.findViewById<TextView>(R.id.hijriDayText)
        val lineView = view.findViewById<View>(R.id.lineView)
        val layoutDates = view.findViewById<LinearLayoutCompat>(R.id.layoutDates)

        textView.text = day.day.numberLocale()
        hijriTextview.text = day.hijriDay?.numberLocale()

        val calendar = java.util.Calendar.getInstance()
        // Set the specific year, month, and day for the calendar instance
        calendar.set(day.year, day.month, day.day.toInt())

        val isFriday = calendar.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.FRIDAY

        if (day.isActive) {
            Log.e("day.isActive","OK")
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_white))
            textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_active)
            textView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(textView.context,R.color.deen_primary))
        }
        else if (day.isInactive) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.deen_white))
            textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_active)
            textView.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(textView.context,R.color.deen_brand_error))
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

        // Detect Current Day

        if(isCurrentDate(day))
        {
            layoutDates.setBackgroundResource(R.drawable.deen_rectangular_border)
        }
        else
        {
            layoutDates.setBackgroundResource(0)
        }

        // Hide Line View

        if (isLastRow(position)) {
            lineView.visibility = View.GONE
        } else {
            lineView.visibility = View.VISIBLE
        }

        return view
    }

    private fun isLastRow(position: Int): Boolean {
        val totalRows = (days.size + 6) / 7
        val rowIndex = position / 7
        return rowIndex == totalRows - 1
    }

    fun updateDays(newDays: List<CalendarDay>) {
        days = newDays
        notifyDataSetChanged()
    }

    private fun isCurrentDate(day: CalendarDay): Boolean{
        val today = Calendar.getInstance()
        return day.day.toInt() == today.get(Calendar.DAY_OF_MONTH) &&
                day.month == today.get(Calendar.MONTH) &&
                day.year == today.get(Calendar.YEAR)
    }
}