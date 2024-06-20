package com.deenislamic.sdk.service.network.response.podcast.category

import androidx.annotation.Keep

@Keep
internal data class PodcastCatResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)