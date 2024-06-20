package com.deenislamic.sdk.service.network.response.common.subcatcardlist

import androidx.annotation.Keep

@Keep
internal data class SubCatResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)