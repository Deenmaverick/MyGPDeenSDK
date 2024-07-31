package com.deenislamic.sdk.service.network.response.gphome

import androidx.annotation.Keep

@Keep
internal data class GPHomeResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean
)