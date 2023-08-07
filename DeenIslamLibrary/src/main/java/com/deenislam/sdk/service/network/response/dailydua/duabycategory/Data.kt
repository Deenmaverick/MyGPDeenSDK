package com.deenislam.sdk.service.network.response.dailydua.duabycategory

import androidx.annotation.Keep

@Keep
internal data class Data(
    val DuaId :Int,
    val Address: Any,
    val Category: String,
    val ImageUrl: String,
    val Language: String,
    val Latitude: Any,
    val Longitude: Any,
    val Pronunciation: String,
    val Serial: Int,
    val SubCategory: Any,
    val SubCategoryName: String,
    val Text: String,
    val TextInArabic: String,
    val Title: String,
    val contentBaseUrl: String,
    var IsFavorite: Boolean
)