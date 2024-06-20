package com.deenislamic.sdk.service.callback

import com.deenislamic.sdk.service.network.response.quran.qurangm.surahlist.Data

internal interface SurahCallback
{
    fun surahClick(surahListData: Data)
    {

    }
}