package com.deenislam.sdk.views.adapters.prayer_times;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.libs.notification.NotificationPermission
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


internal class WidgetPrayerTimes(
    private val callback: prayerTimeAdapterCallback?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var prayerData: PrayerTimesResponse? = null
    private var dateWisePrayerNotificationData:ArrayList<PrayerNotification>?= null
    private var prayerMomentRangeData: PrayerMomentRange? = null
    private var prayerTrackingData:Data ? = null
    private var todayDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_prayer_time_main, parent, false)
        )

    override fun getItemCount(): Int = 6

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
        this.prayerMomentRangeData = prayerMomentRangeData
        todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())

        Log.e("updateData",Gson().toJson(dateWisePrayerNotificationData))

        notifyDataSetChanged()
    }

    fun updateTrackingData(data: Data)
    {
        prayerData?.Data?.WaktTracker?.forEach {

            Log.e("updateTrackingData",Gson().toJson(it))
            when(it.Wakt)
            {
                "Fajr" -> it.status = data.Fajr
                "Zuhr" -> it.status = data.Zuhr
                "Asar" -> it.status = data.Asar
                "Maghrib" -> it.status = data.Maghrib
                "Isha" -> it.status = data.Isha
            }

        }

        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)

            val prayerCheck: RadioButton = itemView.findViewById(R.id.prayerCheck)
            val disableRadio: AppCompatImageView = itemView.findViewById(R.id.disableRadio)
            val icSun: AppCompatImageView = itemView.findViewById(R.id.icSun)
            val prayerName: AppCompatTextView = itemView.findViewById(R.id.prayerName)
            val prayerNameArabic: AppCompatTextView = itemView.findViewById(R.id.prayerNameArabic)
            val timeTxt:AppCompatTextView = itemView.findViewById(R.id.timeTxt)
            val rightBtn:AppCompatImageView = itemView.findViewById(R.id.rightBtn)

            disableRadio.visible(!("pt"+(position+1)).checkCompulsoryprayerByTag())
            prayerCheck.visible(("pt"+(position+1)).checkCompulsoryprayerByTag())

            rightBtn.setOnClickListener {
                callback?.clickNotification("pt"+(position+1))
            }

            setNotificationState("pt"+(position+1),rightBtn)
            checkPrayerTracker("pt"+(position+1),prayerCheck)


            when(position+1)
            {

                1->
                {
                    prayerName.text = "Fajr".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة الفجْر"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_sunrise_fill))
                    timeTxt.text = ("${prayerData?.Data?.Fajr?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${(prayerData?.Data?.Sunrise?.StringTimeToMillisecond()?.minus(60000L))?.MilliSecondToStringTime()?:"0:00"}").numberLocale()

                }
                2->
                {
                    prayerName.text = "Sunrise".prayerMomentLocale()
                    prayerNameArabic.text = "الشروق"
                    prayerCheck.visible(false)
                    disableRadio.visible(true)
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_fajr_light))

                    timeTxt.text = "${prayerData?.Data?.Sunrise?.StringTimeToMillisecond()?.MilliSecondToStringTime()?:"0:00"}".numberLocale()

                }

                3->
                {
                    prayerName.text = "Dhuhr".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة الظُّهْر"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_sun_fill))

                    timeTxt.text = ("${prayerData?.Data?.Juhr?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Asr?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime()?:"0:00"}").numberLocale()


                }

                4->
                {
                    prayerName.text = "Asr".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة العَصر"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_cloud_sun_fill))

                    timeTxt.text = ("${prayerData?.Data?.Asr?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Magrib?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime()?:"0:00"}").numberLocale()

                }

                5->
                {
                    prayerName.text = "Maghrib".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة المَغرب"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_sunset_fill))

                    timeTxt.text = ("${prayerData?.Data?.Magrib?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Isha?.StringTimeToMillisecond()?.minus(300000L)?.MilliSecondToStringTime()?:"0:00"}").numberLocale()




                }

                6->
                {
                    prayerName.text = "Isha".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة العِشاء"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_isha))

                    timeTxt.text = ("${prayerData?.Data?.Isha?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Tahajjut?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime()?:"0:00"}").numberLocale()

                }


                /*6->
                {
                    prayerName.text = "Sunset"
                    prayerNameArabic.text = "غروب"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_sunset_fill))

                }

                7->
                {
                    prayerName.text = "Isha"
                    prayerNameArabic.text = "صلاة العِشاء"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context,R.drawable.ic_night))

                }*/
            }

            if(prayerMomentRangeData?.MomentName == prayerName.text && prayerData?.Data?.Date?.contains(todayDate) == true)
                (itemView.rootView as MaterialCardView).strokeWidth = 1.dp
            else
                (itemView.rootView as MaterialCardView).strokeWidth = 0

            val prayerIsChecked = prayerCheck.isChecked
            prayerCheck.setOnClickListener {

                prayerData?.Data?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy")
                    ?.let {

                            it1 ->

                        val prayer_tag = "pt"+(position+1)

                        val notifyTime = prayerData?.let { getPrayerTimeTagWise(prayer_tag, it1, it) }

                        notifyTime?.let {

                            if(notifyTime >= 0L) {
                                prayerCheck.context.toast("এখনও ${prayer_tag.getWaktNameByTag().prayerMomentLocaleForToast()} ওয়াক্ত শুরু হয়নি")
                                prayerCheck.isChecked = prayerIsChecked
                                return@setOnClickListener
                            }
                            else if(todayDate != it1)
                            {
                                prayerCheck.context.toast("দুঃখিত পূর্ববর্তী তারিখ ট্র্যাক করা সম্ভব নয়")
                                prayerCheck.isChecked = prayerIsChecked
                                return@setOnClickListener
                            }
                        }

                        callback?.prayerCheck("pt"+(position+1),it1, !prayerIsChecked)

                    }
            }

        }
    }

    private fun setNotificationState(prayer_tag: String, rightBtn: AppCompatImageView) {

        rightBtn.setImageDrawable(AppCompatResources.getDrawable(rightBtn.context,R.drawable.ic_notifications_off))

        dateWisePrayerNotificationData?.forEach {

            if (it.prayer_tag == prayer_tag
                && it.date == (prayerData?.Data?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"))
                && NotificationPermission().getInstance().isNotificationPermitted())
            {

                Log.e("setNotificationState",it.state.toString()+" "+prayer_tag)


                prayerData?.let {
                    if(getPrayerTimeTagWise(
                            prayer_tag= prayer_tag,
                            date = it.Data.Date.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"),
                            data = it)<=0) return

                }
                when(it.state)
                {
                    2->
                    {
                        if(NotificationPermission().getInstance().hasAlarm(rightBtn.context,it.id))
                            rightBtn.setImageDrawable(AppCompatResources.getDrawable(rightBtn.context,R.drawable.ic_notifications_default))
                    }
                    3->
                    {
                        if(NotificationPermission().getInstance().hasAlarm(rightBtn.context,it.id))
                            rightBtn.setImageDrawable(AppCompatResources.getDrawable(rightBtn.context,R.drawable.ic_notifications_sound))
                    }

                }
            }


        }

    }

    private fun checkPrayerTracker(prayer_tag:String,view:RadioButton)
    {
        val checkTrack = prayerData?.Data?.WaktTracker?.indexOfFirst {
                    it.Wakt == prayer_tag.getWaktNameByTag() && it.status

        }

        /* if(checkTrack!=1)
          Log.e("prayerNotificationData", Gson().toJson(dateWisePrayerNotificationData?.get(checkTrack!!)))
  */

        view.isChecked = (checkTrack!=null && checkTrack >=0 && prayer_tag.checkCompulsoryprayerByTag())

    }

}

