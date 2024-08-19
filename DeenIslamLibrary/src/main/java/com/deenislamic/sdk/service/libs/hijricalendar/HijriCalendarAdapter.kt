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
import com.deenislamic.sdk.service.network.response.islamiccalendar.CalendarData
import com.deenislamic.sdk.utils.numberLocale
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

        if (day.day.isBlank()) {
            textView.visibility = View.INVISIBLE
            hijriTextview.visibility = View.INVISIBLE
        } else {
            // Existing code for displaying days
            textView.visibility = View.VISIBLE
            hijriTextview.visibility = View.VISIBLE
            textView.text = day.day.numberLocale()
            hijriTextview.text = day.hijriDay?.numberLocale()

            val calendar = java.util.Calendar.getInstance()
            calendar.set(day.year, day.month, day.day.toInt())

            val isFriday = calendar.get(java.util.Calendar.DAY_OF_WEEK) == Calendar.FRIDAY

            if (day.isActive) {
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
                    MonthType.NEXT -> textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_ash))
                }
                textView.setBackgroundResource(0)
            }

            if (isCurrentDate(day)) {
                layoutDates.setBackgroundResource(R.drawable.deen_rectangular_border)
            } else {
                layoutDates.setBackgroundResource(0)
            }

            if (isLastRow(position)) {
                lineView.visibility = View.GONE
            } else {
                lineView.visibility = View.VISIBLE
            }
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

    fun submitList(newDays: List<CalendarData>) {
        val calendar = Calendar.getInstance()

        // Assuming the newDays list has data for the current month
        val firstDayOfMonth = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).parse(newDays.first().Date)!!
        calendar.time = firstDayOfMonth
        val monthNumber = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        // Calculate the starting day of the month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

        // Generate the full list of CalendarDay objects
        val calendarDays = mutableListOf<CalendarDay>()

        // Add days from the previous month
        if (firstDayOfWeek > 0) {
            calendar.add(Calendar.MONTH, -1)
            val prevMonthMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            for (i in 0 until firstDayOfWeek) {
                val prevDate = Calendar.getInstance().apply {
                    set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), prevMonthMaxDay - firstDayOfWeek + 1 + i)
                }
                calendarDays.add(CalendarDay("", calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.PREVIOUS))
            }
            calendar.add(Calendar.MONTH, 1)  // Move back to the current month
        }

        // Add days of the current month
        newDays.forEach { calendarData ->
            calendarDays.add(CalendarDay(
                day = calendarData.Date.substring(8, 10), // Extract day from Date
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR),
                monthType = MonthType.CURRENT,
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
                isActive = false, // Update based on your logic
                isInactive = false, // Update based on your logic
                hijriDay = extractHijriDay(calendarData.arabicDate)
            ))
        }

        // Add days from the next month to fill the grid
        val totalDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val totalCellsNeeded = firstDayOfWeek + totalDaysInMonth
        val cellsInLastRow = totalCellsNeeded % 7
        val cellsNeededForFullRow = if (cellsInLastRow > 0) 7 - cellsInLastRow else 0
        val totalCellsToGenerate = totalCellsNeeded + cellsNeededForFullRow

        val remainingCells = totalCellsToGenerate - calendarDays.size

        // Add days from the next month
        calendar.add(Calendar.MONTH, 1)  // Move to the next month
        for (i in 1..remainingCells) {
            calendarDays.add(CalendarDay("", calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.NEXT))
        }

        // Update the adapter's days list
        days = calendarDays
        notifyDataSetChanged()
    }

    private fun extractHijriDay(hijriDate: String): String? {
        // Define a regex pattern to match the numeric day part of the Hijri date
        val regex = Regex("""(\d+)""")
        val match = regex.find(hijriDate)
        return match?.groupValues?.get(1)
    }
}