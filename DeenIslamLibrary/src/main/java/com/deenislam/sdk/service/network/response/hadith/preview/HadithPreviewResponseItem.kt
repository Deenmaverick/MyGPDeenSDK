package com.deenislam.sdk.service.network.response.hadith.preview

internal data class HadithPreviewResponseItem(
    val annotations: Any,
    val arabicURN: Int,
    val babName: String,
    val babNumber: String,
    val bookID: String,
    val bookName: String,
    val bookNumber: Int,
    val collection: String,
    val grade1: Any,
    val hadithNumber: String,
    val hadithText: String,
    val last_updated: Any,
    val matchingEnglishURN: Int,
    val ourHadithNumber: Int,
    val volumeNumber: Int
)