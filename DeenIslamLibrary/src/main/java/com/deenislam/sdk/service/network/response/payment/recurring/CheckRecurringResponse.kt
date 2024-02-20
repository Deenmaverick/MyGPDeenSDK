package com.deenislam.sdk.service.network.response.payment.recurring

import androidx.annotation.Keep

@Keep
internal data class CheckRecurringResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean
)