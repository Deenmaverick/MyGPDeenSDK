package com.deenislam.sdk.service.network.response.common.subcatcardlist

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.common.subcatcardlist.Data

@Keep
internal data class SubCatResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)