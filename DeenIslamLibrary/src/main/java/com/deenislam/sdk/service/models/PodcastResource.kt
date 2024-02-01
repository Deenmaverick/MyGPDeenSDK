package com.deenislam.sdk.service.models

import androidx.annotation.Keep

internal interface PodcastResource {

    object YoutubeVideoFetchFailed:PodcastResource

    @Keep
    data class FetchLiveVideoUrl(val VideoUrl:String): PodcastResource
}