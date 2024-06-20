package com.deenislamic.sdk.service.network.response.prayerlearning

import androidx.annotation.Keep

@Keep
internal data class PrayerLearningAllCategory(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)