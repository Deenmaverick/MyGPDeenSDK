package com.deenislam.sdk.views.adapters.prayer_times;

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.MilliSecondToStringTime
import com.deenislam.sdk.utils.StringTimeToMillisecond
import com.deenislam.sdk.utils.visible
import com.deenislam.sdk.views.base.BaseViewHolder

internal class WidgetForbiddenTimes : RecyclerView.Adapter<BaseViewHolder>() {

    private var prayerData: PrayerTimesResponse? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prayer_time_alt, parent, false)
        )

    override fun getItemCount(): Int = 2

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun updateData(data: PrayerTimesResponse)
    {
        prayerData = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)
            val prayerCheck: RadioButton = itemView.findViewById(R.id.prayerCheck)
            val disableRadio: AppCompatImageView = itemView.findViewById(R.id.disableRadio)
            val redDisableRadio: AppCompatImageView = itemView.findViewById(R.id.redDisableRadio)
            val icSun: AppCompatImageView = itemView.findViewById(R.id.icSun)
            val prayerName: AppCompatTextView = itemView.findViewById(R.id.prayerName)
            val timeTxt: AppCompatTextView = itemView.findViewById(R.id.timeTxt)
            val rightBtn:AppCompatImageView = itemView.findViewById(R.id.rightBtn)

            icSun.visible(false)
            prayerCheck.visible(false)
            disableRadio.visible(false)
            redDisableRadio.visible(true)
            rightBtn.visible(false)

            when(position+1)
            {
                1->
                {
                    prayerName.text = "Sunrise"
                    timeTxt.text = "${prayerData?.Data?.Sunrise?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Ishrak?.StringTimeToMillisecond()?.MilliSecondToStringTime()?:"0:00"}"

                }

                2->
                {
                    prayerName.text = "Midday"
                    timeTxt.text = "${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Juhr?.StringTimeToMillisecond()?.MilliSecondToStringTime()?:"0:00"}"

                }

                /*3->
                {
                    prayerName.text = "Sunset"
                    timeTxt.text = "${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Juhr?.StringTimeToMillisecond()?.MilliSecondToStringTime()?:"0:00"}"

                    timeTxt.text = "6:30 - 6:33 AM"
                }*/

            }

        }
    }
}
