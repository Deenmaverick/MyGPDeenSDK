package com.deenislam.sdk.service.network.response.dailydua.alldua

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Category: String,
    val Id: Int,
    val ImageUrl: String,
    val Language: String
)