package com.deenislam.sdk.service.network.response.payment.recurring

import androidx.annotation.Keep

@Keep
internal data class Data(
    val ChargeAmount: String,
    val EndDate: String,
    val MSISDN: String,
    val Reference: Any,
    val ServiceID: Int,
    val ServiceName: String,
    val StartDate: String,
    val Status: String,
    val SubscriptionID: String,
    val isSubscribe: Boolean
)