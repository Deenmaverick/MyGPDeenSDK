package com.deenislam.sdk.service.network.response.hadith.chapter

import androidx.annotation.Keep
import com.deenislam.sdk.service.network.response.hadith.chapter.Book
@Keep
internal data class Data(
    val book: List<Book>,
    val bookNumber: String,
    val hadithEndNumber: Int,
    val hadithStartNumber: Int,
    val numberOfHadith: Int
)