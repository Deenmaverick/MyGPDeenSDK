package com.deenislam.sdk.service.network.response.islamimasail.catlist

import androidx.annotation.Keep

@Keep
internal data class Data(
    val ECount: Int,
    val Id: Int,
    val ImageUrl: String,
    val category: String,
    val contentBaseUrl: String
)