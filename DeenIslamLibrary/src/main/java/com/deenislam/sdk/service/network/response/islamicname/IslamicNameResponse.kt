package com.deenislam.sdk.service.network.response.islamicname

import com.deenislam.sdk.service.network.response.islamicname.Data

internal data class IslamicNameResponse(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)