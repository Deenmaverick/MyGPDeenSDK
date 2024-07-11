package com.deenislamic.sdk.service.network.response.islamicbook

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val about: String,
    val authorId: Int,
    val authorImage: String,
    val authorName: String,
    val categoryId: Int,
    val categoryImage: String,
    val categoryName: String,
    val contentBaseUrl: String,
    val contenturl: String,
    val duration: String,
    val imageurl1: String,
    val pronunciation: String,
    val reference: String,
    val referenceurl: String,
    val text: String,
    val textinarabic: String,
    val title: String,
    var IsFavorite: Boolean,
    var IsDownloaded: Boolean
)