package com.deenislam.sdk.service.network.response.ramadan

import androidx.annotation.Keep

@Keep
internal data class FastTime(
    val Date: String,
    val Day: String,
    val Iftaar: String,
    val Suhoor: String,
    val isToday: Boolean,
    var islamicDate: String
)