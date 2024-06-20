package com.deenislamic.sdk.service.network.response.dailydua.todaydua

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Address: String,
    val Category: String,
    val DuaId: Int,
    val ImageUrl: String,
    var IsFavorite: Boolean,
    val Language: String,
    val Latitude: String,
    val Longitude: String,
    val Pronunciation: String,
    val Serial: Int,
    val SubCategory: Int,
    val SubCategoryName: String,
    val Text: String,
    val TextInArabic: String,
    val Title: String,
    val contentBaseUrl: String
)