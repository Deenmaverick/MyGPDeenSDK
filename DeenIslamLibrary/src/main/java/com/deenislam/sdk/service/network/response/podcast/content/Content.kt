package com.deenislam.sdk.service.network.response.podcast.content

import androidx.annotation.Keep

@Keep
internal data class Content(
    val About: String,
    val BannerURL: String,
    val Id: Int,
    val ImageUrl: String,
    val PodcastId: Int,
    val PreviewURL: String,
    val Pronunciation: String,
    val RefUrl: String,
    val Text: String,
    val TextInArabic: String,
    val Title: String
)