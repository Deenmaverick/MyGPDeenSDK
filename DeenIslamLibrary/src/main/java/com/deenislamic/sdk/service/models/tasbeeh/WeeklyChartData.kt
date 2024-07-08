package com.deenislamic.sdk.service.models.tasbeeh

import androidx.annotation.Keep

@Keep
data class WeeklyChartData(
    val date: String,
    val totalDuaCount: Int
)
