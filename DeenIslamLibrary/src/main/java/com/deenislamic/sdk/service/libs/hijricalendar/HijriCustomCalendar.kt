package com.deenislamic.sdk.service.libs.hijricalendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.Keep
import com.deenislamic.sdk.R
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class HijriCustomCalendar(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val calendarGrid: NonScrollableGridView
    private val calendarAdapter: HijriCalendarAdapter
    private var currentMonthNumber: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var activeDaySet: Array<Int> = emptyArray()
    private var inactiveDaySet: Array<Int> = emptyArray()

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.custom_hijri_calendar_layout, this, true)

        calendarGrid = findViewById(R.id.hijriCalendarGrid)

        val daysInCurrentMonth = getCurrentMonthDays(currentYear, currentMonthNumber)
        calendarAdapter = HijriCalendarAdapter(context, daysInCurrentMonth)
        calendarGrid.adapter = calendarAdapter
    }

    private fun getCurrentMonthDays(
        year: Int,
        monthNumber: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
        activeDays: Array<Int> = emptyArray(),
        inactiveDays: Array<Int> = emptyArray()
    ): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val calendar = Calendar.getInstance()
        val locale = Locale.getDefault()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthNumber - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        val totalCellsNeeded = firstDayOfWeek + calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        val validDateRangeStart = Calendar.getInstance().apply { set(1937, 2, 14) } // March 14, 1937
        val validDateRangeEnd = Calendar.getInstance().apply { set(2077, 10, 16) } // November 16, 2077

        // Add days from the previous month
        calendar.add(Calendar.MONTH, -1)
        val prevMonthMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until firstDayOfWeek) {
            val prevDate = Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), prevMonthMaxDay - firstDayOfWeek + 1 + i)
            }
            val hijriDay = if (prevDate in validDateRangeStart..validDateRangeEnd) getHijriDateSafe(prevDate, locale) else null
            days.add(CalendarDay((prevMonthMaxDay - firstDayOfWeek + 1 + i).toString(), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.PREVIOUS, hijriDay = hijriDay))
        }

        // Add days of the current month
        calendar.add(Calendar.MONTH, 1)
        val maxDaysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDaysInCurrentMonth) {
            val currentDate = Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), i)
            }
            val hijriDay = if (currentDate in validDateRangeStart..validDateRangeEnd) getHijriDateSafe(currentDate, locale) else null
            days.add(
                CalendarDay(i.toString(), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.CURRENT, calendar.get(Calendar.DAY_OF_WEEK),
                isActive = i in activeDays,
                isInactive = i in inactiveDays,
                hijriDay = hijriDay
            )
            )
        }

        // If we've entered a 6th row, let's fill it up entirely with the next month's days.
        val cellsInLastRow = totalCellsNeeded % 7
        val cellsNeededForFullRow = if (cellsInLastRow > 0) 7 - cellsInLastRow else 0
        val totalCellsToGenerate = totalCellsNeeded + cellsNeededForFullRow

        val remainingCells = totalCellsToGenerate - days.size

        // Add days from the next month
        calendar.add(Calendar.MONTH, 1)  // Move to the next month
        for (i in 1..remainingCells) {
            val nextDate = Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), i)
            }
            val hijriDay = if (nextDate in validDateRangeStart..validDateRangeEnd) getHijriDateSafe(nextDate, locale) else null
            days.add(
                CalendarDay(i.toString(), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.NEXT, calendar.get(Calendar.DAY_OF_WEEK),
                hijriDay = hijriDay
            )
            )
        }

        return days
    }

    private fun getHijriDateSafe(date: Calendar, locale: Locale): String? {
        return try {
            val hijriCalendar = UmmalquraCalendar()
            hijriCalendar.time = date.time

            // Format the day number according to the locale
            val hijriDay = hijriCalendar.get(UmmalquraCalendar.DAY_OF_MONTH)
            String.format(locale, "%d", hijriDay)
        } catch (e: IllegalArgumentException) {
            null // Handle exceptions as needed
        }
    }

    fun setActiveDays(activeDays: Array<Int>) {
        // Update the active days set
        activeDaySet = activeDays
        updateCalendarData()
    }

    fun setInactiveDays(inactiveDays: Array<Int>) {
        // Update the inactive days set
        inactiveDaySet = inactiveDays
        updateCalendarData()
    }

    fun updateCalendarData() {
        // Get days based on the current month number and updated active days
        val updatedDays = getCurrentMonthDays(currentYear, currentMonthNumber, activeDaySet, inactiveDaySet)
        calendarAdapter.updateDays(updatedDays)
    }

    fun setMonth(date: String) {
        try {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
            val parsedDate = dateFormat.parse(date)
                ?: throw IllegalArgumentException("Invalid date format: $date. It should be in the format yyyy/MM/dd.")

            val calendar = Calendar.getInstance()
            calendar.time = parsedDate

            val monthNumber = calendar.get(Calendar.MONTH) + 1  // Adding 1 because Calendar.MONTH is 0-indexed
            currentYear = calendar.get(Calendar.YEAR)

            if (monthNumber in 1..12) {
                currentMonthNumber = monthNumber
                val updatedDays = getCurrentMonthDays(currentYear, currentMonthNumber, activeDaySet, inactiveDaySet)
                calendarAdapter.updateDays(updatedDays)
            } else {
                throw IllegalArgumentException("Invalid month number: $monthNumber. It should be between 1 and 12.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        updateCalendarData()
    }
}
@Keep
data class CalendarDay(
    val day: String,
    val month: Int,
    val year: Int,
    val monthType: MonthType,
    val dayOfWeek: Int? = null, // Add this line
    val isActive: Boolean = false,
    val isInactive: Boolean = false,
    val isNA: Boolean = false,
    val hijriDay: String? = null
)


@Keep
enum class MonthType {
    PREVIOUS, CURRENT, NEXT
}