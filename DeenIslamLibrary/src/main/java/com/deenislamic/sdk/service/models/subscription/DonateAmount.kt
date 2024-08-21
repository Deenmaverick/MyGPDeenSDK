package com.deenislamic.sdk.service.models.subscription

import androidx.annotation.Keep

@Keep
internal data class DonateAmount(
    val amount:Int,
    val customID:String,
    var isActive:Boolean = false
)
