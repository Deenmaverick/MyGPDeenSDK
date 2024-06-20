package com.deenislamic.sdk.service.network.response

import androidx.annotation.Keep

@Keep
internal data class BasicResponse(
    val Data: Any,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)