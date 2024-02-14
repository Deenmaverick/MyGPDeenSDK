package com.deenislam.sdk.service.callback.quran

import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data

internal interface QuranOfflineDownloadCallback {

    fun playBtnClicked(getData: Data)
    fun deleteOfflineQuran(folderLocation: String, absoluteAdapterPosition: Int)
}