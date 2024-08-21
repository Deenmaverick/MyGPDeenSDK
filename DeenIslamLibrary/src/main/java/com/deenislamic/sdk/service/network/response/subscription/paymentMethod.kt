package com.deenislamic.sdk.service.network.response.subscription

import androidx.annotation.Keep

@Keep
internal data class paymentMethod(
    val isBkash: Boolean,
    val isGpay: Boolean,
    val isNagad: Boolean,
    val isGP: Boolean,
    val isRobi: Boolean,
    val isSSL: Boolean
)