package com.deenislamic.sdk.service.network.response.payment

import androidx.annotation.Keep

@Keep
internal data class DCBChargeResponse(
    val message: String,
    val statusCode: Int,
    val data:Data?
){
    @Keep
    data class Data(
        val paymentUrl: String,
    )
}