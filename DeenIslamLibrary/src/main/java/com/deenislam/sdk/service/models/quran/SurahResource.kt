package com.deenislam.sdk.service.models.quran

import com.deenislam.sdk.service.network.response.quran.SurahListData

 interface SurahResource
{

    data class getSurahList(val data: List<SurahListData>):SurahResource
    data class getSurahListnew(val data: List<SurahListData>):SurahResource
}