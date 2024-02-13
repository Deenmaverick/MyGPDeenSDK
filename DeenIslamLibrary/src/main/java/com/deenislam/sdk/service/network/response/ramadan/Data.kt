package com.deenislam.sdk.service.network.response.ramadan

import androidx.annotation.Keep

@Keep
internal data class Data(
    val FastTime: List<FastTime>,
    val FastTracker: FastTracker,
    val RamadanDua: List<RamadanDua>
)