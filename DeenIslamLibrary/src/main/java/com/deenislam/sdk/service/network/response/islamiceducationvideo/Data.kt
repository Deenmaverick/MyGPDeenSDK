package com.deenislam.sdk.service.network.response.islamiceducationvideo

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.common.CommonCardData

@Keep
internal data class Data(
    val Content: List<CommonCardData>,
    val Recent: List<CommonCardData>
)