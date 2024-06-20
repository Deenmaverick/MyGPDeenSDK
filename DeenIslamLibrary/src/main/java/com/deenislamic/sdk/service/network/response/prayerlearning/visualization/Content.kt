package com.deenislamic.sdk.service.network.response.prayerlearning.visualization

import androidx.annotation.Keep

@Keep
internal data class Content(
    val Category: String,
    val CategoryId: Int,
    val Id: Int,
    val ImageUrl: String,
    val Language: String,
    val Pronunciation: Any,
    val Serial: Int,
    val Text: String,
    val TextInArabic: Any,
    val Title: String
)