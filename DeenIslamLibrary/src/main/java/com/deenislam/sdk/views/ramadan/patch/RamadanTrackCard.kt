package com.deenislam.sdk.views.ramadan.patch;

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatTextView
import com.deenislam.sdk.R
import com.deenislam.sdk.service.callback.RamadanCallback
import com.deenislam.sdk.service.network.response.ramadan.FastTracker
import com.deenislam.sdk.utils.CallBackProvider
import com.deenislam.sdk.utils.dayNameLocale
import com.deenislam.sdk.utils.dp
import com.deenislam.sdk.utils.monthNameLocale
import com.deenislam.sdk.utils.numberLocale
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator

internal class RamadanTrackCard(itemView: View, private val fastTracker: FastTracker) {

    private val monthlyTrackerTxt:AppCompatTextView = itemView.findViewById(R.id.monthlyTrackerTxt)
    private val datetime:AppCompatTextView = itemView.findViewById(R.id.datetime)
    private val arabicDate:AppCompatTextView = itemView.findViewById(R.id.arabicDate)
    private val suhoorTimeTxt:AppCompatTextView = itemView.findViewById(R.id.suhoorTimeTxt)
    private val iftarTimetxt:AppCompatTextView = itemView.findViewById(R.id.iftarTimetxt)
    private val fastingCheck:RadioButton = itemView.findViewById(R.id.fastingCheck)
    private val fastingProgress:LinearProgressIndicator = itemView.findViewById(R.id.fastingProgress)
    private val ramadan_complete_txt:AppCompatTextView = itemView.findViewById(R.id.ramadan_complete_txt)
    private val suhoorCardview:MaterialCardView = itemView.findViewById(R.id.suhoorCardview)
    private val iftarCardview:MaterialCardView = itemView.findViewById(R.id.iftarCardview)
    private var callback = CallBackProvider.get<RamadanCallback>()
    private var isFasting = false

    init {

        (itemView.layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = 12.dp

        datetime.text = fastTracker.Date.numberLocale().monthNameLocale().dayNameLocale()
        arabicDate.text = fastTracker.islamicDate
        suhoorTimeTxt.text = "${fastTracker.Suhoor}".numberLocale()
        iftarTimetxt.text = "${fastTracker.Iftaar}".numberLocale()
        fastingCheck.isChecked = fastTracker.isFasting
        fastingProgress.max = fastTracker.totalDays
        fastingProgress.progress = fastTracker.totalTracked
        ramadan_complete_txt.text = "${fastTracker.totalTracked}/${fastTracker.totalDays}".numberLocale()

        isFasting = fastingCheck.isChecked

        fastingCheck.setOnClickListener {
            callback?.setFastingTrack(!isFasting)
        }

        monthlyTrackerTxt.setOnClickListener {
            callback?.openMonthlyTracker()
        }

        suhoorCardview.setOnClickListener {
            Log.e("suhoorCardview",callback.toString())
            callback?.sehriCardClicked(fastTracker.Suhoor)
        }

        iftarCardview.setOnClickListener {

            callback?.iftarCardClicked(fastTracker.Iftaar)
        }
    }

    fun updateFastingTrack(fasting: Boolean)
    {
        if(fasting)
            fastTracker.totalTracked++
        else
            fastTracker.totalTracked--

        fastTracker.isFasting = fasting


        fastingProgress.progress = fastTracker.totalTracked
        ramadan_complete_txt.text = "${fastTracker.totalTracked}/${fastTracker.totalDays}"
        fastingCheck.isChecked = fasting
        isFasting = fasting
    }

    fun getTrackData() = fastTracker


}