package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.quran.qurannew.surah.Chapter

internal interface SurahCallback
{
    fun surahClick(surahListData: Chapter)
}