package com.deenislamic.sdk.service.network.response.subscription

import androidx.annotation.Keep

@Keep
internal data class PremiumFeature(
    val featureName: String,
    val featureSubText: String
)