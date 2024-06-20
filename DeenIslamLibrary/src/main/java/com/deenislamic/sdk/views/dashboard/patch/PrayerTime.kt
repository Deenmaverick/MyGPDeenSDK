package com.deenislamic.sdk.views.dashboard.patch

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.deenislamic.sdk.R
import com.deenislamic.sdk.service.models.prayer_time.PrayerMomentRange
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.utils.*
import com.deenislamic.sdk.utils.getPrayerTimeName
import com.deenislamic.sdk.views.adapters.common.CommonStateList
import com.deenislamic.sdk.views.adapters.dashboard.PrayerTimeCallback
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class PrayerTime(view: View,private var prayerData: PrayerTimesResponse?) {

    private var timefor:AppCompatTextView = view.findViewById(R.id.timefor)
    private val prayerBG: AppCompatImageView = view.findViewById(R.id.prayerBG)
    private val prayerMoment: AppCompatTextView = view.findViewById(R.id.prayerMoment)
    private val prayerMomentRange: AppCompatTextView = view.findViewById(R.id.appCompatTextView)
    private val nextPrayerName: AppCompatTextView = view.findViewById(R.id.nextPrayer)
    private val nextPrayerTime: AppCompatTextView = view.findViewById(R.id.nextPrayerTime)
    private val allPrayer: LinearLayout = view.findViewById(R.id.allPrayer)
    private val askingLy: AppCompatTextView = view.findViewById(R.id.askingLy)
    private val progressTxt: AppCompatTextView = view.findViewById(R.id.progressTxt)
    private val namazTask: LinearProgressIndicator = view.findViewById(R.id.namazTask)
    private val prayerCheck: RadioButton = view.findViewById(R.id.prayerCheck)
    private val stateBtn:LinearLayout = view.findViewById(R.id.stateBtn)
    private val stateTxt:AppCompatTextView = view.findViewById(R.id.stateTxt)
    private var commonStateList:CommonStateList ? = null
    private var callback = CallBackProvider.get<PrayerTimeCallback>()

    init {
        prayerBG.setBackgroundColor(ContextCompat.getColor(prayerBG.context, R.color.deen_black))

        allPrayer.setOnClickListener {
            Log.e("ALLPTCALLBACK",callback.toString())
            callback?.allPrayerPage()
        }

        view.setOnClickListener {
            callback?.allPrayerPage()
        }

        callback?.billboard_prayer_load_complete()
    }

    fun load()
    {

        val getContext = prayerBG.context

        prayerData?.Data?.state?.let {
            stateTxt.text = it
        }

        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
        val prayerMomentRangeData: PrayerMomentRange? =  prayerData?.let { getPrayerTimeName(it,currentTime.stringTimeToEpochTime() /*getPrayerTimeName(it,"08:01:45 PM".StringTimeToMillisecond("hh:mm:ss aa") */ ) }

        askingLy.text = getContext.resources.getString(R.string.billboard_have_you_prayed,prayerMomentRangeData?.MomentName?.prayerMomentLocale())
        prayerTracker(true)

        val get_prayer_tag = get_prayer_tag_by_name(prayerMomentRangeData?.MomentName.toString())

        val checkTrack = prayerData?.Data?.WaktTracker?.indexOfFirst {
            it.Wakt == get_prayer_tag.getWaktNameByTag() && it.status

        }
        prayerMomentRange.text = prayerMomentRangeData?.StartTime?.timeLocale() +" - " + prayerMomentRangeData?.EndTime?.timeLocale()


        //prayerCheck.isEnabled = !(checkTrack!=null && checkTrack >=0)
        prayerCheck.isChecked = (checkTrack!=null && checkTrack >=0)

        timefor.show()
        prayerMoment.setPadding(0,0,0,0)
        prayerMomentRange.show()


        if(prayerMomentRangeData?.MomentName == "Fajr")
            prayerBG.setBackgroundResource(R.drawable.fajr)
        else if(prayerMomentRangeData?.MomentName == "Dhuhr")
            prayerBG.setBackgroundResource(R.drawable.dhuhr)
        else if(prayerMomentRangeData?.MomentName == "Asr")
            prayerBG.setBackgroundResource(R.drawable.asr)
        else if(prayerMomentRangeData?.MomentName == "Maghrib")
            prayerBG.setBackgroundResource(R.drawable.maghrib)
        else if(prayerMomentRangeData?.MomentName == "Isha")
            prayerBG.setBackgroundResource(R.drawable.isha)
        else if(prayerMomentRangeData?.MomentName == "Ishraq") {
            prayerTracker(false)
            prayerBG.setBackgroundResource(R.drawable.fajr)
        } else if(prayerMomentRangeData?.MomentName == "Chasht") {
            prayerTracker(false)
            prayerBG.setBackgroundResource(R.drawable.fajr)
        } else if(prayerMomentRangeData?.MomentName == getContext?.getString(R.string.forbidden_time)){
            prayerTracker(false)
            prayerBG.setBackgroundResource(R.drawable.deen_bg_forbidden_prayer)
        } else {
            //prayerBG.setBackgroundResource(R.drawable.isha)
            prayerTracker(false)
            prayerMomentRange.text = "--"
            timefor.hide()
            prayerMoment.setPadding(0,13.dp,0,0)
            prayerMomentRange.hide()
            prayerBG.setBackgroundColor(
                ContextCompat.getColor(getContext,
                    R.color.deen_black
                )
            )

        }
        progressTxt.text = "${getCompletedPrayerCount()}/5".numberLocale()
        prayerMoment.text = prayerMomentRangeData?.MomentName?.prayerMomentLocale()
        namazTask.progress = getCompletedPrayerCount()*20
        nextPrayerName.text = getContext.resources.getString(R.string.billboard_next_prayer,prayerMomentRangeData?.NextPrayerName?.prayerMomentLocale()?:"--")
        nextPrayerTime.text = "-00:00:00".timeLocale()


        val prayerIsChecked = prayerCheck.isChecked

        prayerCheck.setOnClickListener {
            prayerCheck.isChecked = !prayerCheck.isChecked
            callback?.prayerTask(get_prayer_tag,!prayerIsChecked)
        }

        prayerMomentRangeData?.nextPrayerTimeCount?.let {
            if(it>0) {
                nextPrayerTime.text = "-"+prayerMomentRangeData.nextPrayerTimeCount?.TimeDiffForPrayer()?.numberLocale()
                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer = object : CountDownTimer(it, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        nextPrayerTime.text = "-" + millisUntilFinished.TimeDiffForPrayer().numberLocale()
                    }

                    override fun onFinish() {
                        com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.cancel()
                        callback = CallBackProvider.get<PrayerTimeCallback>()
                        callback?.nextPrayerCountownFinish()
                    }
                }
                com.deenislamic.sdk.utils.singleton.CountDownTimer.prayerTimer?.start()
            }
        }

        if(commonStateList == null)
        commonStateList = CommonStateList(stateBtn)
    }

    private fun prayerTracker(bol:Boolean)
    {
        askingLy.visible(bol)
        prayerCheck.visible(bol)
        namazTask.visible(bol)
        progressTxt.visible(bol)
    }

    private fun getCompletedPrayerCount():Int
    {
        var count = 0
        prayerData?.Data?.WaktTracker?.let {
            it.forEach {
                    it1->
                if(it1.status) {
                    count++
                }
            }
        }

        return  count
    }

    fun stateSelected(stateModel: StateModel) {
        Log.e("PrayerTimeLoc",Gson().toJson(stateModel))
        stateTxt.text = stateModel.stateValue
        commonStateList?.stateSelected(stateModel)
    }

    fun updatePrayerTime(data: PrayerTimesResponse) {
        prayerData = data
        load()

    }

}
