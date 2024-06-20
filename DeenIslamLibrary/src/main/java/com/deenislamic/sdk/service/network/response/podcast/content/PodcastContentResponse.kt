package com.deenislamic.sdk.service.network.response.podcast.content

import androidx.annotation.Keep

@Keep
internal data class PodcastContentResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)