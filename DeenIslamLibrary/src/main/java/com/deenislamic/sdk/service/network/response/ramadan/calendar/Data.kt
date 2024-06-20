package com.deenislamic.sdk.service.network.response.ramadan.calendar

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Month: String,
    val calander: List<Calander>,
    val islamicMonthEnd: String,
    val islamicMonthStart: String
)