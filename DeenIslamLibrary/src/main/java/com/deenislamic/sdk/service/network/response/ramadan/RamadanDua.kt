package com.deenislamic.sdk.service.network.response.ramadan

import androidx.annotation.Keep

@Keep
internal data class RamadanDua(
    val Address: String,
    val Category: String,
    val DuaId: Int,
    val ImageUrl: String,
    val IsFavorite: Boolean,
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
    val contentBaseUrl: String,
    val reference: String
)