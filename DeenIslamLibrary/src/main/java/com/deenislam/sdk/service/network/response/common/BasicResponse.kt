package com.deenislam.sdk.service.network.response.common

import androidx.annotation.Keep

@Keep
data class BasicResponse(
    val Data: String,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)