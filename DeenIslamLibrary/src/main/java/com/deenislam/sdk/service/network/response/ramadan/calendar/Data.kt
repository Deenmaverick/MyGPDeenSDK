package com.deenislam.sdk.service.network.response.ramadan.calendar

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.ramadan.calendar.Calander

@Keep
internal data class Data(
    val Month: String,
    val calander: List<Calander>,
    val islamicMonthEnd: String,
    val islamicMonthStart: String
)