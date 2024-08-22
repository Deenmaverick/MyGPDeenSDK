package com.deenislamic.sdk.service.libs.trackercalendar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.Keep
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

internal class TrackerCalendar(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

//    private val calendarGrid: NonScrollableGridView
    private var calendarAdapter: CalendarTrackerAdapter
    // Store the current month number (default is the current month)
    private var currentMonthNumber: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    // Store the active days
    private var activeDaySet: Array<Int> = emptyArray()
    private var inactiveDaySet: Array<Int> = emptyArray()

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.tracker_calendar_layout, this, true)

//        calendarGrid = findViewById(R.id.calendarGrid)

        val daysInCurrentMonth = getCurrentMonthDays(currentYear)
//        calendarAdapter = CalendarTrackerAdapter(context, daysInCurrentMonth)
//        calendarGrid.adapter = calendarAdapter

        val calendarRecyclerView = findViewById<RecyclerView>(R.id.calendarRecyclerView)

        val gridLayoutManager = GridLayoutManager(context, 7)  // 7 columns for days of the week
        calendarRecyclerView.layoutManager = gridLayoutManager
        calendarAdapter = CalendarTrackerAdapter(context, daysInCurrentMonth)
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun getCurrentMonthDays(year: Int, monthNumber: Int = Calendar.getInstance().get(
        Calendar.MONTH) + 1, activeDays: Array<Int> = emptyArray(), inactiveDays: Array<Int> = emptyArray()): List<CalendarTrackerDay> {
        val days = mutableListOf<CalendarTrackerDay>()
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthNumber - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
        val totalCellsNeeded = firstDayOfWeek + calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Add days from the previous month
        calendar.add(Calendar.MONTH, -1)
        val prevMonthMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until firstDayOfWeek) {
            days.add(
                CalendarTrackerDay((prevMonthMaxDay - firstDayOfWeek + 1 + i).toString(), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.PREVIOUS
                )
            )
        }

        // Add days of the current month
        calendar.add(Calendar.MONTH, 1)
        val maxDaysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..maxDaysInCurrentMonth) {
            days.add(
                CalendarTrackerDay(i.toString(), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.CURRENT, calendar.get(
                Calendar.DAY_OF_WEEK),
                isActive = i in activeDays,
                isInactive = i in inactiveDays,
                todayDate = currentDay
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
            days.add(
                CalendarTrackerDay(i.toString(), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR), MonthType.NEXT, calendar.get(
                Calendar.DAY_OF_WEEK))
            )
        }

        /*for (day in days) {
            Log.e("CalendarDays", "${day.day} - ${day.monthType}")
        }*/

        return days
    }

    fun getCalendarAdapter(): CalendarTrackerAdapter {
        return calendarAdapter
    }

    fun setMonth(date: String) {
        try {

            val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
            val parsedDate = dateFormat.parse(date)
                ?: throw IllegalArgumentException("Invalid date format: $date. It should be in the format yyyy/MM/dd.")

            val calendar = Calendar.getInstance()
            calendar.time = parsedDate

            val monthNumber =
                calendar.get(Calendar.MONTH) + 1  // Adding 1 because Calendar.MONTH is 0-indexed
            currentYear = calendar.get(Calendar.YEAR)

            if (monthNumber in 1..12) {
                currentMonthNumber = monthNumber
                val updatedDays = getCurrentMonthDays(
                    currentYear,
                    currentMonthNumber,
                    activeDaySet,
                    inactiveDaySet
                )  // You'll need to update the function signature
                calendarAdapter.updateDays(updatedDays)
            } else {
                throw IllegalArgumentException("Invalid month number: $monthNumber. It should be between 1 and 12.")
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun updatePrayerData(prayerTrackers: List<Data>,selectedDate: String) {
        Log.e("comedata",prayerTrackers.toString())
        val updatedDays = calendarAdapter.days.map { day ->
            val prayerTracker = prayerTrackers.find { tracker ->
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(tracker.TrackingDate)
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.get(Calendar.DAY_OF_MONTH) == day.day.toInt() &&
                        calendar.get(Calendar.MONTH) == day.month &&
                        calendar.get(Calendar.YEAR) == day.year
            }

            if (prayerTracker != null) {
                // Count how many prayers were completed
                val checkedCount = listOf(prayerTracker.Fajr, prayerTracker.Zuhr, prayerTracker.Asar, prayerTracker.Maghrib, prayerTracker.Isha).count { it }
                day.copy(isChekcked = checkedCount > 0, progressLevel = checkedCount, isSelected = selectedDate) // Update isChecked and progressLevel
            } else {
                day
            }
        }
        calendarAdapter.updateDays(updatedDays) // Notify adapter of the updates
    }

    fun incrementProgressForToday(targetDate: String) {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH).toString()
        val currentMonth = today.get(Calendar.MONTH)
        val currentYear = today.get(Calendar.YEAR)

        val updatedDays = calendarAdapter.days.map { day ->
            if (day.day == targetDate && day.month == currentMonth && day.year == currentYear) {
                // Increment progressLevel
                val newProgressLevel = (day.progressLevel + 1) % 6 // Assuming max progress is 4
                day.copy(progressLevel = newProgressLevel)
            } else {
                day
            }
        }

        calendarAdapter.updateDays(updatedDays) // Notify adapter of the updates
    }

    fun decrementProgressForToday(targetDate: String) {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH).toString()
        val currentMonth = today.get(Calendar.MONTH)
        val currentYear = today.get(Calendar.YEAR)

        val updatedDays = calendarAdapter.days.map { day ->
            if (day.day == targetDate && day.month == currentMonth && day.year == currentYear) {
                // Increment progressLevel
                val newProgressLevel = (day.progressLevel - 1) % 6 // Assuming max progress is 4
                day.copy(progressLevel = newProgressLevel)
            } else {
                day
            }
        }

        calendarAdapter.updateDays(updatedDays) // Notify adapter of the updates
    }

    fun showPreviousMonth() {
        currentMonthNumber -= 1
        if (currentMonthNumber < 1) {
            currentMonthNumber = 12
            currentYear -= 1
        }
        val updatedDays = getCurrentMonthDays(currentYear, currentMonthNumber, activeDaySet, inactiveDaySet)
        calendarAdapter.updateDays(updatedDays)
    }

    fun showNextMonth() {
        currentMonthNumber += 1
        if (currentMonthNumber < 1) {
            currentMonthNumber = 12
            currentYear += 1
        }
        val updatedDays = getCurrentMonthDays(currentYear, currentMonthNumber, activeDaySet, inactiveDaySet)
        calendarAdapter.updateDays(updatedDays)
    }
}

@Keep
internal data class CalendarTrackerDay(
    val day: String,
    val month: Int,
    val year: Int,
    val monthType: MonthType,
    val dayOfWeek: Int? = null, // Add this line
    val isActive: Boolean = false,
    val isInactive: Boolean = false,
    val isSelected: String? = null,
    val isNA: Boolean = false,
    val todayDate: Int? = null,
    val isChekcked: Boolean = false,
    val progressLevel: Int = 0
)


@Keep
internal enum class MonthType {
    PREVIOUS, CURRENT, NEXT
}