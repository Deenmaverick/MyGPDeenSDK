package com.deenislam.sdk.service.network.response.allah99name

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.allah99name.Data

@Keep
internal data class Allah99NameResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)