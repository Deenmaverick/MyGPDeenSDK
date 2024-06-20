package com.deenislamic.sdk.service.network.response.zakat

import androidx.annotation.Keep

@Keep
internal data class SavedZakatResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)