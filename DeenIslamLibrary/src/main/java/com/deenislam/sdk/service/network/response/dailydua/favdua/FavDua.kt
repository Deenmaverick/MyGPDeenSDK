package com.deenislam.sdk.service.network.response.dailydua.favdua

internal data class FavDua(
    val Data: List<Data>?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)