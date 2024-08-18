package com.deenislamic.sdk.service.models.ramadan

import androidx.annotation.Keep


@Keep
data class StateModel(
    val state:String,
    val stateValue:String,
    val statebn:String = ""
)
