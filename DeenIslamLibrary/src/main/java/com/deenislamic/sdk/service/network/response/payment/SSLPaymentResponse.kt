package com.deenislamic.sdk.service.network.response.payment

import androidx.annotation.Keep

@Keep
internal data class SSLPaymentResponse(
    val GatewayPageURL: String,
    val errorCode: String,
    val errorMessage: String,
    val status: String,
    val subscription_id: String
)