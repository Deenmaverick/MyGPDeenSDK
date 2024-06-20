package com.deenislamic.sdk.service.network.response.islamiceducationvideo

import androidx.annotation.Keep

@Keep
internal data class IslamiceducationVideoResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)