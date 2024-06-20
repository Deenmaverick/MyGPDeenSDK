package com.deenislamic.sdk.service.network.response.boyan.videopreview

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Id: Int,
    val about: String,
    val categoryId: Int,
    val categoryImage: String,
    val categoryName: String,
    val contenturl: String,
    val duration: String,
    val imageurl1: String,
    val pronunciation: String,
    val reference: String,
    val referenceurl: String,
    val scholarId: Int,
    val scholarImage: String,
    val scholarName: String,
    val text: String,
    val textinarabic: String,
    val title: String
)