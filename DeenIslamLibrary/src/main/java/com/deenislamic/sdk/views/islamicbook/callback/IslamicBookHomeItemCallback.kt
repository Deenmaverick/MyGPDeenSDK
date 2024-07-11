package com.deenislamic.sdk.views.islamicbook.callback

import android.graphics.Bitmap

internal interface IslamicBookHomeItemCallback {
    fun bookItemClieked(Id: Int, contentUrl: String, bookName: String)
    fun bookLikeClicked(Id: Int, categoryId: Int, title: String, type: String)
    fun bookDownloadClickd(
        contentUrl: String,
        id: Int,
        title: String,
        mText: String,
        bitmap: Bitmap?
    )
    fun bookDownloadCancelClicked(notificationId: Int)
}