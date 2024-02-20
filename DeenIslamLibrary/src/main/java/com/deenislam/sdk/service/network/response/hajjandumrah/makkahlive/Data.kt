package com.deenislam.sdk.service.network.response.hajjandumrah.makkahlive

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val ImageUrl: String,
    val Reference: String,
    val Text: String,
    val Title: String
)