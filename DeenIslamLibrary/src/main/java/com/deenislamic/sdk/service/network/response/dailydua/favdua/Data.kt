package com.deenislamic.sdk.service.network.response.dailydua.favdua

import androidx.annotation.Keep

@Keep
internal data class Data(
    val Address: Any,
    val Category: String,
    val DuaId: Int,
    val ImageUrl: String,
    val IsFavorite: Boolean,
    val Language: String,
    val Latitude: Any,
    val Longitude: Any,
    val Pronunciation: String,
    val Serial: Int,
    val SubCategory: Int,
    val SubCategoryName: String,
    val Text: String,
    val TextInArabic: String,
    val Title: String,
    val contentBaseUrl: String,
    val Source:String,
    val Transliteration:String,
    val Benefit:String,
    var isExpanded:Boolean = false
)