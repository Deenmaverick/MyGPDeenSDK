package com.deenislamic.sdk.service.models.ramadan

import androidx.annotation.Keep


@Keep
internal data class StateModel(
    val state:String,
    val stateValue:String,
    val statebn:String = ""
)
