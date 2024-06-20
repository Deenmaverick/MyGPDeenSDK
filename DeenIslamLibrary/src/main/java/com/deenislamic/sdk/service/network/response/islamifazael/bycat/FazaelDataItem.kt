package com.deenislamic.sdk.service.network.response.islamifazael.bycat

import androidx.annotation.Keep

@Keep
internal data class FazaelDataItem(
    val Category: String,
    val FavoriteCount: Int,
    val Id: Int,
    val IsFavorite: Boolean,
    val about: String,
    val imageurl: String,
    val language: String,
    val pronunciation: String,
    val reference: String,
    val serial: Int,
    val subcategory: String,
    val subcategoryId: Int,
    val text: String,
    val textinarabic: String,
    val title: String
)