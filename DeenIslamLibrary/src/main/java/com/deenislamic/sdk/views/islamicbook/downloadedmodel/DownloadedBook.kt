package com.deenislamic.sdk.views.islamicbook.downloadedmodel

import android.graphics.Bitmap
import androidx.annotation.Keep

@Keep
internal data class DownloadedBook(
    val bookId: String,
    val bookName: String,
    val bookAuthorName: String,
    var bookImage: Bitmap? = null
)
