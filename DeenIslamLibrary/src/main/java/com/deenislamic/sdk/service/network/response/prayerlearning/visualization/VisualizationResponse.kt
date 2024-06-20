package com.deenislamic.sdk.service.network.response.prayerlearning.visualization

import androidx.annotation.Keep

@Keep
internal data class VisualizationResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)