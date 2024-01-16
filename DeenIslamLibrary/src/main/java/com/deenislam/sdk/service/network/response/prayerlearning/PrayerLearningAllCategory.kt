package com.deenislam.sdk.service.network.response.prayerlearning

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.prayerlearning.Data

@Keep
internal data class PrayerLearningAllCategory(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)