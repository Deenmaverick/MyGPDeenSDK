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
import com.deenislam.sdk.R
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.libs.notification.NotificationPermission
import com.deenislam.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.utils.*
import com.deenislam.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

internal class WidgetOtherPrayerTimes(
    private val callback: prayerTimeAdapterCallback?
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var prayerData: PrayerTimesResponse? = null
    private var dateWisePrayerNotificationData:ArrayList<PrayerNotification>?= null
    private var prayerMomentRangeData: PrayerMomentRange? = null
    private var todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prayer_time_alt, parent, false)
        )

    override fun getItemCount(): Int = 4

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
        todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
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
                callback?.clickNotification("opt"+(position+1))
            }

            setNotificationState("opt"+(position+1),rightBtn)
            checkPrayerTracker("opt"+(position+1),prayerCheck)


            when(position+1)
            {
                1->
                {
                    prayerName.text = "Tahajjud"

                    timeTxt.text = "${prayerData?.Data?.Tahajjut?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Fajr?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime()?:"0:00"}"

                }

                2->
                {
                    prayerName.text = "Suhoor (End)"
                    timeTxt.text = "${prayerData?.Data?.Sehri?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}"

                }

                3->
                {
                    prayerName.text = "Ishraq"
                    timeTxt.text = "${prayerData?.Data?.Ishrak?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm")?:"0:00"}"+" - "+
                            "${prayerData?.Data?.Noon?.StringTimeToMillisecond()?.minus(60000L)?.MilliSecondToStringTime()?:"0:00"}"

                }

                4->
                {
                    prayerName.text = "Iftaar (Start)"
                    timeTxt.text = "${prayerData?.Data?.Magrib?.StringTimeToMillisecond()?.MilliSecondToStringTime("hh:mm aa")?:"0:00"}"
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
            Log.e("notifyDAta", Gson().toJson(it))
            if (it.prayer_tag == prayer_tag
                && it.date == (prayerData?.Data?.Date?.formateDateTime("yyyy-MM-dd'T'HH:mm:ss","dd/MM/yyyy"))
                && NotificationPermission().getInstance().isNotificationPermitted())
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
