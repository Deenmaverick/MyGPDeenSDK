package com.deenislamic.sdk.service.network.response.podcast.comment

import androidx.annotation.Keep

@Keep
internal data class Comment(
    val CTime: String,
    val CommentCount: Int,
    val Id: Int,
    val LikeCount: Int,
    val MSISDN: String?,
    val PodcastId: Int,
    val Text: String,
    val UImage: String,
    val UName: String,
    val isLiked: Boolean,
    val isSocial: Boolean
)