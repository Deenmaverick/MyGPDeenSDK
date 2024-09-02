package com.deenislamic.sdk.views.adapters.prayertracker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.callback.PrayerTrackerCallback
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.network.response.prayertimes.calendartracker.PrayerTrackerResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislamic.sdk.utils.checkCompulsoryprayerByTag
import com.deenislamic.sdk.utils.dp
import com.deenislamic.sdk.utils.formateDateTime
import com.deenislamic.sdk.utils.getLocalContext
import com.deenislamic.sdk.utils.getPrayerTrackerTagWise
import com.deenislamic.sdk.utils.getWaktNameByTag
import com.deenislamic.sdk.utils.prayerMomentLocale
import com.deenislamic.sdk.utils.prayerMomentLocaleForToast
import com.deenislamic.sdk.utils.toast
import com.deenislamic.sdk.utils.visible
import com.deenislamic.sdk.views.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal class PrayerTrackerAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private lateinit var prayerTrackerInterface: PrayerTrackerCallback
    private var prayerData: PrayerTrackerResponse? = null
    private var dateWisePrayerNotificationData: ArrayList<PrayerNotification>? = null
    private var prayerMomentRangeData: PrayerMomentRange? = null
    private var prayerTrackingData: Data? = null
    private lateinit var targetDate: String
    private var todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())


    fun setOnButtonClickListener(listener: PrayerTrackerCallback) {
        this.prayerTrackerInterface = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context.getLocalContext())
                .inflate(R.layout.item_prayer_tracker, parent, false)
        )

    override fun getItemCount(): Int = 6

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun updateData(
        prayerTimesResponse: PrayerTrackerResponse,
        notificationData: ArrayList<PrayerNotification>?,
        prayerMomentRangeData: PrayerMomentRange?,
        targetDate: String
    ) {
        prayerData = prayerTimesResponse
        dateWisePrayerNotificationData = notificationData
        this.prayerMomentRangeData = prayerMomentRangeData
        this.targetDate = targetDate
        Log.e("checkdate",targetDate)
        //todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(Date())

        notifyDataSetChanged()
    }

    fun cleanData(){
        prayerData = null
        dateWisePrayerNotificationData?.clear()
        prayerMomentRangeData = null
        prayerTrackingData = null
        targetDate = ""
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun onBind(position: Int) {
            super.onBind(position)

            val prayerCheck: RadioButton = itemView.findViewById(R.id.prayerTrackerCheck)
            val disableRadio: AppCompatImageView = itemView.findViewById(R.id.disableRadio)
            val icSun: AppCompatImageView = itemView.findViewById(R.id.icSun)
            val prayerName: AppCompatTextView = itemView.findViewById(R.id.prayerName)
            val prayerNameArabic: AppCompatTextView = itemView.findViewById(R.id.prayerNameArabic)

            disableRadio.visible(!("pt" + (position + 1)).checkCompulsoryprayerByTag())
            prayerCheck.visible(("pt" + (position + 1)).checkCompulsoryprayerByTag())

            checkPrayerTracker("pt" + (position + 1), prayerCheck)

            when (position + 1) {
                1 -> {
                    prayerName.text = "Fajr".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة الفجْر"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context, R.drawable.ic_sunrise_fill))
                }
                2 -> {
                    prayerName.text = "Sunrise".prayerMomentLocale()
                    prayerNameArabic.text = "الشروق"
                    prayerCheck.visible(false)
                    disableRadio.visible(true)
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context, R.drawable.ic_fajr_light))
                }
                3 -> {
                    prayerName.text = "Dhuhr".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة الظُّهْر"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context, R.drawable.ic_sun_fill))
                }
                4 -> {
                    prayerName.text = "Asr".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة العَصر"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context, R.drawable.ic_cloud_sun_fill))
                }
                5 -> {
                    prayerName.text = "Maghrib".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة المَغرب"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context, R.drawable.ic_sunset_fill))
                }
                6 -> {
                    prayerName.text = "Isha".prayerMomentLocale()
                    prayerNameArabic.text = "صلاة العِشاء"
                    icSun.setImageDrawable(AppCompatResources.getDrawable(icSun.context, R.drawable.ic_isha))
                }
            }

            // Update stroke width based on target date
            if (prayerMomentRangeData?.MomentName == prayerName.text && prayerData?.Data?.prayerTime?.Date?.contains(targetDate) == true) {
                (itemView.rootView as MaterialCardView).strokeWidth = 1.dp
            } else {
                (itemView.rootView as MaterialCardView).strokeWidth = 0
            }

            val prayerIsChecked = prayerCheck.isChecked

            prayerCheck.setOnClickListener {


                prayerData?.Data?.prayerTime?.Date?.let { dateStr ->

                    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                    val serverMonth = serverDateFormat.parse(dateStr)

                    val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                    val formattedDateStr = outputDateFormat.format(serverMonth)


                    val prayer_tag = "pt" + (position + 1)
                    if (formattedDateStr != todayDate) {
                        // If the date is not in the target month, uncheck the RadioButton
                        prayerCheck.isChecked = false
                        itemView.context.toast(itemView.context.getString(R.string.track_only_current_month_s_namaz))
                    } else {
                        val formattedTargetDate = targetDate.formateDateTime("yyyy-MM-dd", "dd/MM/yyyy")
                        val tracker = prayerData?.Data?.tracker?.firstOrNull {
                            it.TrackingDate.formateDateTime("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy") == formattedTargetDate
                        }

                        tracker?.let { matchedTracker ->
                            matchedTracker.TrackingDate.formateDateTime("yyyy-MM-dd'T'HH:mm:ss", "dd/MM/yyyy")?.let { it1 ->
                                val notifyTime = prayerData?.let { getPrayerTrackerTagWise(prayer_tag, it1, it) }

                                notifyTime?.let {
                                    if (notifyTime >= 0L) {
                                        prayerCheck.isChecked = false
                                        itemView.context.toast(itemView.context.getString(R.string.waktNotStartedToast, prayer_tag.getWaktNameByTag().prayerMomentLocaleForToast()))
                                    } else {
                                        prayerTrackerInterface.prayerTrack("pt" + (position + 1), it1, !prayerIsChecked)
                                    }
                                }
                            }
                        } ?: run {
                            // Handle the case where no matching tracker is found
                            itemView.context.toast("No matching tracker found for the target date.")
                        }
                    }
                }
            }
        }
    }

    private fun checkPrayerTracker(prayer_tag: String, view: RadioButton) {
        val tracker = prayerData?.Data?.tracker?.firstOrNull { it.TrackingDate.contains(targetDate) }
        Log.e("checkwaqt",prayerData?.Data?.tracker.toString())
        when (prayer_tag.getWaktNameByTag()) {

            "Fajr" -> {
                if (tracker?.Fajr == true) view.isChecked = true
                if (tracker?.Fajr == false) view.isChecked = false
            }
            "Zuhr" ->
            {
                if (tracker?.Zuhr == true) view.isChecked = true
                if (tracker?.Zuhr == false) view.isChecked = false
            }
            "Asar" ->
            {
                if (tracker?.Asar == true) view.isChecked = true
                if (tracker?.Asar == false) view.isChecked = false
            }
            "Maghrib" ->
            {
                if (tracker?.Maghrib == true) view.isChecked = true
                if (tracker?.Maghrib == false) view.isChecked = false
            }
            "Isha" ->
            {
                if (tracker?.Isha == true) view.isChecked = true
                if (tracker?.Isha == false) view.isChecked = false
            }

            else -> view.isChecked = false // Default to unchecked if no match
        }
    }
}