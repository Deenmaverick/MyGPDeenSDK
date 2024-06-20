package com.deenislamic.sdk.service.network.response.islamimasail.answer

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val IsFavorite: Boolean,
    val Place: String?,
    val QRaiserName: String,
    val QuestionID: Int,
    val TimeStamp: String,
    val categoryId: Int,
    val categoryName: String,
    val contenturl: String,
    val favCount: Int,
    val huzur: String,
    val huzurId: Any,
    val imageurl: String,
    val isAnonymous: Boolean,
    val isUrgent: Boolean,
    val language: String,
    val msisdn: String,
    val pronunciation: String,
    val reference: String,
    val text: String,
    val textinarabic: String,
    val title: String,
    val viewCount: Int
)