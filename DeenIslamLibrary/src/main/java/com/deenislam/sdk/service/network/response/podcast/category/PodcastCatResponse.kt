package com.deenislam.sdk.service.network.response.podcast.category

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.podcast.category.Data

@Keep
internal data class PodcastCatResponse(
    val Data: List<Data>,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)