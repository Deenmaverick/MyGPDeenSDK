package com.deenislamic.sdk.service.network.response.hajjandumrah.makkahlive

import androidx.annotation.Keep

@Keep
internal data class MakkahLiveVideoResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)