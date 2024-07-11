package com.deenislamic.sdk.service.models.islamicbook

import androidx.annotation.Keep

@Keep
internal data class BookDownloadPayload(
    val filenameByUser: String?,
    val progress: Int,
    val isComplete: Boolean,
    val notificationId: Int,
    val isCancelled:Boolean = false,
)
