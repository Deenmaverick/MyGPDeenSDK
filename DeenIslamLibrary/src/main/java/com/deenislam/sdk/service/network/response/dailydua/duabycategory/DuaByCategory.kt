package com.deenislam.sdk.service.network.response.dailydua.duabycategory

import androidx.annotation.Keep

@Keep
internal data class DuaByCategory(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)