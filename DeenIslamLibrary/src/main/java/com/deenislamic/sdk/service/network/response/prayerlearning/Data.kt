package com.deenislamic.sdk.service.network.response.prayerlearning

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Category: String,
    val Id: Int,
    val ImageUrl: String,
    val IsActive: Boolean,
    val Language: String,
    val Serial: Int
)