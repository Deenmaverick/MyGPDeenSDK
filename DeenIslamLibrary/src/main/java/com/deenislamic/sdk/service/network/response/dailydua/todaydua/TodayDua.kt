package com.deenislamic.sdk.service.network.response.dailydua.todaydua

import androidx.annotation.Keep

@Keep
internal data class TodayDua(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)