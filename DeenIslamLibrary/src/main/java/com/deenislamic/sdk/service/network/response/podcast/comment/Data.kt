package com.deenislamic.sdk.service.network.response.podcast.comment

import androidx.annotation.Keep

@Keep
internal data class Data(
    val CommentCount: Int,
    val comments: List<Comment>
)