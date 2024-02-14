package com.deenislam.sdk.service.callback.common

internal interface DownloaderCallback {

    fun updateDownloadProgress(
        filenameByUser: String?,
        progress: Int,
        isComplete: Boolean,
        notificationId: Int,
        isCancelled: Boolean = false,
    ) {

    }
}