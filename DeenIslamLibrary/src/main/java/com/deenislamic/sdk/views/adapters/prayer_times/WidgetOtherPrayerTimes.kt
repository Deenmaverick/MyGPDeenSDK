package com.deenislamic.sdk.views.adapters.prayer_times;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class WidgetOtherPrayerTimes(
    private val callback: prayerTimeAdapterCallback?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var prayerData: PrayerTimesResponse? = null
    private var dateWisePrayerNotificationData:ArrayList<PrayerNotification>?= null
    private var prayerMomentRangeData: PrayerMomentRange? = null
    private var todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_prayer_time_alt, parent, false)
        )

    override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun updateData(
        prayerTimesResponse: PrayerTimesResponse,
        notificationData: ArrayList<PrayerNotification>?,
        prayerMomentRangeData: PrayerMomentRange?
    )
    {
        prayerData = prayerTimesResponse
        dateWisePrayerNotificationData = notificationData
        this.prayerMomentRangeData =prayerMomentRangeData
        todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)

            val prayerCheck: RadioButton = itemView.findViewById(R.id.prayerCheck)
            val disableRadio: AppCompatImageView = itemView.findViewById(R.id.disableRadio)
            val icSun: AppCompatImageView = itemView.findViewById(R.id.icSun)
            val prayerName: AppCompatTextView = itemView.findViewById(R.id.prayerName)
            val timeTxt: AppCompatTextView = itemView.findViewById(R.id.timeTxt)
            val rightBtn:AppCompatImageView = itemView.findViewById(R.id.rightBtn)

            icSun.visible(false)
            prayerCheck.visible(false)
            disableRadio.visible(true)

            rightBtn.setOnClickListener {
                callback?.clickNotification("opt"+(absoluteAdapterPosition+1))
            }

            setNotificationState("opt"+(position+1),rightBtn)
            checkPrayerTracker("opt"+(position+1),prayerCheck)


            when(position+1)
            {

                1->
                {
                    prayerName.text = "Suhoor (End)".prayerMomentLocale()
                    timeTxt.text = ("${prayerData?.Data?.Sehri?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}").numberLocale()
                    rightBtn.hide()
                }

                2->
                {
                    prayerName.text = "Iftaar (Start)".prayerMomentLocale()
                    timeTxt.text = ("${prayerData?.Data?.Magrib?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}").numberLocale()

                    rightBtn.hide()
                }


                3->
                {
                    prayerName.text = "Tahajjud End".prayerMomentLocale()

                   /* timeTxt.text = ("${prayerData?.Data?.Tahajjut?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Fajr?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime()?:"0:00"}").numberLocale()
*/

                    timeTxt.text = ("${prayerData?.Data?.Tahajjut?.StringTimeToMillisecond()?.minus(1*60*1000)?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}").numberLocale()

                    rightBtn.hide()

                }


                4->
                {
                    prayerName.text = "Ishraq".prayerMomentLocale()
                    timeTxt.text = ("${prayerData?.Data?.Ishrak?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.minus(7*60*1000)?.MilliSecondToStringTime()?:"0:00"}").numberLocale()

                    rightBtn.hide()

                }

                5->
                {
                    prayerData?.let {

                        val chashtEnd = getChashtStartBD(it)
                        prayerName.text = "Chasht".prayerMomentLocale()
                        timeTxt.text = ("${chashtEnd?.StringTimeToMillisecond()?.MilliSecondToStringTime("h:mm aa")?:"0:00"}"+" - "+
                                "${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.minus(7*60*1000)?.MilliSecondToStringTime("h:mm aa")?:"0:00"}").numberLocale()

                    }

                    rightBtn.hide()

                }


            }

            if(prayerMomentRangeData?.MomentName == prayerName.text && prayerData?.Data?.Date?.contains(todayDate) == true)
                (itemView.rootView as MaterialCardView).strokeWidth = 1.dp
            else
                (itemView.rootView as MaterialCardView).strokeWidth = 0

        }
    }

    private fun setNotificationState(prayer_tag: String, rightBtn: AppCompatImageView) {

       rightBtn.setImageDrawable(AppCompatResources.getDrawable(rightBtn.context,R.drawable.ic_notifications_off))

        dateWisePrayerNotificationData?.forEach {
           // Log.e("notifyDAta",prayer_tag+ Gson().toJson(it))
            if (it.prayer_tag == prayer_tag
                && it.date == (prayerData?.Data?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"))
                /*&& NotificationPermission().getInstance().isNotificationPermitted()*/)
            {

                prayerData?.let {
                    if(getPrayerTimeTagWise(
                            prayer_tag= prayer_tag,
                            date = it.Data.Date.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"),
                            data = it) <=0) return

                }

                when(it.state)
                {
                    2->  rightBtn.setImageDrawable(AppCompatResources.getDrawable(rightBtn.context,R.drawable.ic_notifications_default))
                    3->  rightBtn.setImageDrawable(AppCompatResources.getDrawable(rightBtn.context,R.drawable.ic_notifications_sound))

                }
            }

        }

    }

    private fun checkPrayerTracker(prayer_tag:String,view:RadioButton)
    {
        val checkTrack = dateWisePrayerNotificationData?.indexOfFirst { it.isPrayed && it.prayer_tag == prayer_tag && it.prayer_tag.isNotEmpty() && prayer_tag.checkCompulsoryprayerByTag() }

        //Log.e("prayerNotificationData", Gson().toJson(dateWisePrayerNotificationData?.get(checkTrack!!)))

        view.isChecked = (checkTrack!=null && checkTrack >=0)

    }
}
