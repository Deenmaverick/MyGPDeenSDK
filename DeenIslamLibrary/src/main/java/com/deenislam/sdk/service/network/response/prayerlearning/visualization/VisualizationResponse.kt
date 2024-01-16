package com.deenislam.sdk.service.network.response.prayerlearning.visualization

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.prayerlearning.visualization.Data

@Keep
internal data class VisualizationResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)