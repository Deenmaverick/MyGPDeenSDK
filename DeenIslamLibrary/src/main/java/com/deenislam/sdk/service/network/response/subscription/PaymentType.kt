package com.deenislam.sdk.service.network.response.subscription

import androidx.annotation.Keep

@Keep
internal data class PaymentType(
    val isBkashEnable: Boolean,
    val isNagadEnable: Boolean,
    val isSSLEnable: Boolean,
    val packageAmount: String,
    val packageDescription: String,
    val packageHeader: String,
    val packageTitle: String,
    val serviceIDBkash: Int,
    val serviceIDNagad: String,
    val serviceIDSSL: Int,
    val isDataBundle:Boolean = false
)