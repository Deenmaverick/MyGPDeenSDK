package com.deenislam.sdk.service.network.response.boyan.categoriespaging

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val ImageUrl: String,
    val category: String,
    val contentBaseUrl: String
)