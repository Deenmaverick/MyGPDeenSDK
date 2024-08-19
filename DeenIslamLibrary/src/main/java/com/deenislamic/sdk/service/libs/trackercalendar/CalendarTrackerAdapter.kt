package com.deenislamic.sdk.service.libs.trackercalendar

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.PrayerTrackerCallback
import com.deenislamic.sdk.service.libs.circularprogressbar.CircularProgressBar
import com.deenislamic.sdk.utils.CallBackProvider
import com.deenislamic.sdk.utils.numberLocale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class CalendarTrackerAdapter(
    private val context: Context,
    var days: List<CalendarTrackerDay>
) : RecyclerView.Adapter<CalendarTrackerAdapter.CalendarViewHolder>() {

    private var callback = CallBackProvider.get<PrayerTrackerCallback>()
    private val calendar = java.util.Calendar.getInstance()
    private val isToday = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(calendar.time) ==
            SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())

    // ViewHolder class to hold item views
    class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.dayText)
        val parentLayout: FrameLayout = itemView.findViewById(R.id.parentLayout)
        val circularProgressBar: CircularProgressBar = itemView.findViewById(R.id.circularProgressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_tracker_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val day = days[position]

        holder.textView.text = day.day.numberLocale()

        holder.parentLayout.setOnClickListener {
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_primary))
            callback = CallBackProvider.get<PrayerTrackerCallback>()
            callback?.
            prayerDate(day)
        }

        // Set the specific year, month, and day for the calendar instance
        calendar.set(day.year, day.month - 1, day.day.toInt()) // Adjust month by subtracting 1

//        calendar.firstDayOfWeek = java.util.Calendar.SUNDAY

        val isFriday = calendar.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.TUESDAY


        when {
            day.isActive -> {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_white))
                holder.textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_active)
                holder.textView.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(holder.textView.context, R.color.deen_primary)
                )
            }
            day.isSelected == day.day -> {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_primary))
            }
            day.isInactive -> {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_white))
                holder.textView.setBackgroundResource(R.drawable.deen_ic_calendar_day_active)
                holder.textView.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(holder.textView.context, R.color.deen_brand_error)
                )
            }
            day.isNA -> {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_black_deep))
                holder.textView.setBackgroundResource(0)
            }
            isFriday && day.monthType == MonthType.CURRENT -> {
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_brand_error))
                holder.textView.setBackgroundResource(0)
            }
            else -> {
                when (day.monthType) {
                    MonthType.PREVIOUS -> holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_ash))
                    MonthType.CURRENT -> holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_black_deep))
                    MonthType.NEXT -> holder.textView.setTextColor(ContextCompat.getColor(context, R.color.deen_txt_ash))
                }
                holder.textView.setBackgroundResource(0)
            }
        }

        // Update the progress bar with the correct progress level for this day
        holder.circularProgressBar.updateProgress(day.progressLevel)
    }

    override fun getItemCount(): Int = days.size

    fun updateDays(newDays: List<CalendarTrackerDay>) {
        days = newDays
        notifyDataSetChanged()
    }
}