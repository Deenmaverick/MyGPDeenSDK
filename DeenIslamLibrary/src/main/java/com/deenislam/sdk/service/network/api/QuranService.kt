package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.quran.qurannew.surah.SurahList
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

internal interface QuranService {

    @GET("chapters")
    suspend fun getSurahList(@Query("language") language: String,): SurahList

}