package com.deenislam.sdk.utils.singleton

import android.os.CountDownTimer
import java.util.TimerTask

internal object CountDownTimer {

    var ramadanTimer: CountDownTimer?=null
    var prayerTimer: CountDownTimer?=null
    var ramadanFloatingTimer: CountDownTimer?=null
    var ramadanFloatingTask: TimerTask? = null
}