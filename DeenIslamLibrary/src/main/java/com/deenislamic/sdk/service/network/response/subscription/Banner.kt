package com.deenislamic.sdk.service.network.response.subscription

import androidx.annotation.Keep

@Keep
internal data class Banner(
    val Type: String,
    val featureName: String,
    val featureSubText: String
)