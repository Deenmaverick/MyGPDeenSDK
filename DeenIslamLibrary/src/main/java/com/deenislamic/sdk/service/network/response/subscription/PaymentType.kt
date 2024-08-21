package com.deenislamic.sdk.service.network.response.subscription

import androidx.annotation.Keep

@Keep
internal data class PaymentType(
    var isBkashEnable: Boolean,
    var isNagadEnable: Boolean,
    var isSSLEnable: Boolean,
    var isGPEnable:Boolean,
    val packageAmount: String,
    val packageDescription: String,
    val packageHeader: String,
    val packageTitle: String,
    val serviceIDBkash: Int,
    val serviceIDNagad: String,
    val serviceIDGP: String,
    val serviceIDSSL: Int,
    val serviceIDGpay:String,
    val isDataBundle:Boolean = false,
    val isGpayEnable:Boolean,
    val isRobiEnable:Boolean = false,
    val serviceIDRobi:String = "",
    val isRecurring:Boolean = false
)