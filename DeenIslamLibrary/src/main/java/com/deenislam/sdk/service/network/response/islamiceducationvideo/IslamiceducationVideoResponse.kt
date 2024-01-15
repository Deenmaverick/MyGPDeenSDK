package com.deenislamic.service.network.response.islamiceducationvideo

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.islamiceducationvideo.Data

@Keep
data class IslamiceducationVideoResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)