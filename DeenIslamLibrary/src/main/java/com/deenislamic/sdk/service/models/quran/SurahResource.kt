package com.deenislamic.sdk.service.models.quran

import com.deenislamic.sdk.service.network.response.quran.qurannew.surah.Chapter

internal interface SurahResource
{

    data class getSurahList(val data: List<Chapter>):SurahResource
    data class getSurahList_quran_com(val data: List<Chapter>):SurahResource
}