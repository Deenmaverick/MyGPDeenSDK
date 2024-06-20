package com.deenislamic.sdk.service.network.response.hadith

import androidx.annotation.Keep

@Keep
internal data class Data(
    val ArabicTitle: String,
    val HasBooks: Boolean,
    val HasChapters: Boolean,
    val Id: Int,
    val ImageUrl: String,
    val IsActive: Boolean,
    val Language: String,
    val Name: String,
    val Title: String,
    val TotalAvailableHadith: Int,
    val TotalHadith: Int,
    val contentBaseUrl: String
)