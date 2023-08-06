package com.deenislam.sdk.service.network.response.hadith.preview

data class Data(
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