package com.deenislamic.sdk.service.network.response.islamimasail.questionbycat

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val IsFavorite: Boolean,
    val Place: String?,
    val QRaiserName: String,
    val categoryId: Int,
    val categoryName: String,
    val contenturl: String,
    val favCount: Int,
    val imageurl: String,
    val isAnonymous: Boolean,
    val isUrgent: Boolean,
    val language: String,
    val msisdn: String,
    val title: String,
    val viewCount: Int
)