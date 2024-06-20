package com.deenislamic.sdk.service.network.response.advertisement

import androidx.annotation.Keep

@Keep
internal data class AdvertisementResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean
)