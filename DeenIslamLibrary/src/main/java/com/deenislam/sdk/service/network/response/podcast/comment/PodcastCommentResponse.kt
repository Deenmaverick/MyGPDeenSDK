package com.deenislam.sdk.service.network.response.podcast.comment

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.podcast.comment.Data

@Keep
internal data class PodcastCommentResponse(
    val Data: Data?,
    val Message: String,
    val Success: Boolean,
    val TotalData: Int
)