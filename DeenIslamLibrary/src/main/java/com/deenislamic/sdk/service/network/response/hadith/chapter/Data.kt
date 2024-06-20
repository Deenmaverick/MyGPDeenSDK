package com.deenislamic.sdk.service.network.response.hadith.chapter

import androidx.annotation.Keep

@Keep
internal data class Data(
    val ArabicName: String,
    val BookId: Int,
    val ChapterNo: Int,
    val HadithEndNumber: Int,
    val HadithStartNumber: Int,
    val Id: Int,
    val ImageUrl: Any,
    val IsActive: Boolean,
    val Language: String,
    val Name: String,
    val NumberOfHadith: Int,
    val contentBaseUrl: String
)