package com.deenislam.sdk.service.network.response.zakat

internal data class SavedZakatResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)