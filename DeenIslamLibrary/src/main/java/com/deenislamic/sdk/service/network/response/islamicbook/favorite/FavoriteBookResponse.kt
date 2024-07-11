package com.deenislamic.sdk.service.network.response.islamicbook.favorite

import androidx.annotation.Keep

@Keep
internal data class FavoriteBookResponse(
    val Data: Any,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)