package com.deenislamic.sdk.service.network.response.islamicname

import androidx.annotation.Keep

@Keep
internal data class IslamicNameResponse(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)