package com.deenislamic.sdk.service.callback.quran

import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data

internal interface QuranOfflineDownloadCallback {

    fun playBtnClicked(getData: Data)
    fun deleteOfflineQuran(folderLocation: String, absoluteAdapterPosition: Int)
}