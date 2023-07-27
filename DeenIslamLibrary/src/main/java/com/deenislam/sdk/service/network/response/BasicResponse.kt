package com.deenislam.sdk.service.network.response

internal data class BasicResponse(
    val Data: Any,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)