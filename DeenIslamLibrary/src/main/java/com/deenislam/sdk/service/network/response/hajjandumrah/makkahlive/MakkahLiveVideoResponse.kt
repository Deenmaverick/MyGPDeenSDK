package com.deenislam.sdk.service.network.response.hajjandumrah.makkahlive

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.hajjandumrah.makkahlive.Data

@Keep
internal data class MakkahLiveVideoResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)