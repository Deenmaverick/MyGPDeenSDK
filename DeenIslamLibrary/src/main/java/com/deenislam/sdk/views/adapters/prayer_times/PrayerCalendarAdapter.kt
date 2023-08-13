package com.deenislam.sdk.views.adapters.prayer_times;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.prayer_calendar.Data
import com.deenislam.sdk.utils.MilliSecondToStringTime
import com.deenislam.sdk.utils.StringTimeToMillisecond
import com.deenislam.sdk.utils.getLocalContext
import com.deenislam.sdk.utils.monthNameLocale
import com.deenislam.sdk.utils.numberLocale
import com.deenislam.sdk.utils.timeLocale
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*

internal class PrayerCalendarAdapter(
    private val data: ArrayList<Data>
) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_prayer_calendar, parent, false)
        )

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        private val headerLayout:LinearLayout = itemView.findViewById(R.id.headerLayout)
        private val itemlayout:LinearLayout = itemView.findViewById(R.id.itemlayout)
        private val date:AppCompatTextView = itemView.findViewById(R.id.date)
        private val fajr:AppCompatTextView = itemView.findViewById(R.id.fajr)
        private val dhuhr:AppCompatTextView = itemView.findViewById(R.id.dhuhr)
        private val asr:AppCompatTextView = itemView.findViewById(R.id.asr)
        private val maghrib:AppCompatTextView = itemView.findViewById(R.id.maghrib)
        private val isha:AppCompatTextView = itemView.findViewById(R.id.isha)
        val format = SimpleDateFormat("dd MMMM", Locale.ENGLISH) // or you can add before dd/M/yyyy


        override fun onBind(position: Int) {
            super.onBind(position)

            val prayerTime = data[position]

            if((position % 2)==0)
            {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.deen_background))
            }
            else
                itemView.setBackgroundResource(0)


            if(position==0)
                headerLayout.visible(true)
            else
                headerLayout.visible(false)


                val newDate = format.parse(prayerTime.Date.StringTimeToMillisecond("yyyy-MM-dd'T'HH:mm:ss").MilliSecondToStringTime("dd MMMM"))
                date.text = format.format(newDate).numberLocale().monthNameLocale()

                fajr.text = ("${prayerTime.Fajr.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa")?:"0:00"}").timeLocale()
                dhuhr.text = ("${prayerTime.Juhr.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa")?:"0:00"}").timeLocale()
                asr.text = ("${prayerTime.Asr.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa")?:"0:00"}").timeLocale()
                maghrib.text = ("${prayerTime.Magrib.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa")?:"0:00"}").timeLocale()
                isha.text = ("${prayerTime.Isha.StringTimeToMillisecond().MilliSecondToStringTime("hh:mm aa")?:"0:00"}").timeLocale()


        }
    }
}
