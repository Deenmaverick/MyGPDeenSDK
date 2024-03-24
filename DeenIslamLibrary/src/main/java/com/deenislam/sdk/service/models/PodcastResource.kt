package com.deenislam.sdk.service.models

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.dashboard.Data
import com.deenislam.sdk.service.network.response.podcast.comment.Comment

internal interface PodcastResource {

    object YoutubeVideoFetchFailed:PodcastResource

    @Keep
    data class FetchLiveVideoUrl(val VideoUrl:String): PodcastResource

    data class HomePatch(val data: List<Data>) :PodcastResource

    data class SecureUrl(val value: String) : PodcastResource

    data class PodcastContent(val data: com.deenislam.sdk.service.network.response.podcast.content.Data) :PodcastResource
    data class PodcastCat(val data: List<com.deenislam.sdk.service.network.response.podcast.category.Data>) :PodcastResource

    data class PodcastComment(val data: com.deenislam.sdk.service.network.response.podcast.comment.Data) :PodcastResource
    data class PodcastAddComment(val data: com.deenislam.sdk.service.network.response.podcast.comment.Data) :PodcastResource

    data class PodcastCommentLiked(val copy: Comment) :PodcastResource
}