package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.quran.qurannew.surah.SurahList
import retrofit2.http.GET
import retrofit2.http.Query

internal interface QuranService {

    @GET("chapters")
    suspend fun getSurahList(@Query("language") language: String,): SurahList


}