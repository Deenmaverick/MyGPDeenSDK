package com.deenislamic.sdk.views.adapters.prayer_times;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.views.base.BaseViewHolder

internal class WidgetForbiddenTimes : RecyclerView.Adapter<BaseViewHolder>() {

    private var prayerData: PrayerTimesResponse? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_prayer_time_alt, parent, false)
        )

    override fun getItemCount(): Int = 3

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
                    prayerName.text = "Sunrise".prayerMomentLocale()
                    timeTxt.text = ("${prayerData?.Data?.Sunrise?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Ishrak?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}").numberLocale()

                }

                2->
                {
                    prayerName.text = "Midday".prayerMomentLocale()
                    timeTxt.text = ("${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.minus(6*60*1000)?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}").numberLocale()

                }

                3->
                {
                    prayerName.text = prayerName.context.getString(R.string.sunset)
                    timeTxt.text = ("${prayerData?.Data?.Sunset?.StringTimeToMillisecond()?.minus(15*60*1000)?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Sunset?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}").numberLocale()

                    //timeTxt.text = "6:30 - 6:33 AM"
                }

            }

        }
    }
}
