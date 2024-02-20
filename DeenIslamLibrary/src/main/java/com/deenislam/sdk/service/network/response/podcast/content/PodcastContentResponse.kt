package com.deenislam.sdk.service.network.response.podcast.content

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.podcast.content.Data

@Keep
internal data class PodcastContentResponse(
    val Data: Data,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)