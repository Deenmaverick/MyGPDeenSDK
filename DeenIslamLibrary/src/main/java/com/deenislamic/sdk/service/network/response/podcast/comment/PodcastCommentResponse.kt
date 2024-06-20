package com.deenislamic.sdk.service.network.response.podcast.comment

import androidx.annotation.Keep

@Keep
internal data class PodcastCommentResponse(
    val Data: Data?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)