package com.deenislam.sdk.service.network.response.hadith.preview

import androidx.annotation.Keep

@Keep
internal data class Data(
    val BabName: Any,
    val BabNumber: Any,
    val BookId: Int,
    val BookName: String,
    val ChapterName: String,
    val ChapterNo: Int,
    val HadithArabicText: String,
    val HadithNumber: String,
    val HadithText: String,
    val Id: Int,
    val IsActive: Boolean,
    var IsFavorite: Boolean,
    val Language: String,
    val OurHadithNumber: Int
)