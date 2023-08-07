package com.deenislam.sdk.service.network.response.dailydua.alldua

import androidx.annotation.Keep

@Keep
internal data class AllDuaResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)