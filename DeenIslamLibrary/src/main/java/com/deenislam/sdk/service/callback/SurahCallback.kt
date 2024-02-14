package com.deenislam.sdk.service.callback

import com.deenislam.sdk.service.network.response.quran.qurangm.surahlist.Data

internal interface SurahCallback
{
    fun surahClick(surahListData: Data)
    {

    }
}