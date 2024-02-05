package com.deenislam.sdk.service.network.response.dashboard

import androidx.annotation.Keep

@Keep
internal data class DashboardResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val ProfileUrl:String?,
    val isPeronalMenu:Boolean
)