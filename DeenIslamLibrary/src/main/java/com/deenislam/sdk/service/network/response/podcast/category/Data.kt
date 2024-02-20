package com.deenislam.sdk.service.network.response.podcast.category

import androidx.annotation.Keep

@Keep
internal data class Data(
    val About: String,
    val BannerURL: String,
    val Category: String,
    val CategoryId: Int,
    val Contents: Any,
    val Id: Int,
    val ImageUrl: String,
    val IsFavorite: Boolean,
    val IsLive: Boolean,
    val Language: String,
    val PreviewURL: String,
    val Pronunciation: String,
    val RefUrl: String,
    val Serial: Int,
    val Text: String,
    val TextInArabic: String,
    val Title: String,
    val favCount: Int,
    val viewCount: Int
)