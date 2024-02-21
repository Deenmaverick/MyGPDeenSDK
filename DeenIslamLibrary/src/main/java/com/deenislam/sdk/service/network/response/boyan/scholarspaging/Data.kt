package com.deenislam.sdk.service.network.response.boyan.scholarspaging

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val ImageUrl: String,
    val Name: String,
    val contentBaseUrl: String,
    var ECount: Int? = 0
)