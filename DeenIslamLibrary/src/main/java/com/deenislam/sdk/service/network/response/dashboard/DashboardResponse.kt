package com.deenislam.sdk.service.network.response.dashboard

import androidx.annotation.Keep

@Keep
internal data class DashboardResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean
)