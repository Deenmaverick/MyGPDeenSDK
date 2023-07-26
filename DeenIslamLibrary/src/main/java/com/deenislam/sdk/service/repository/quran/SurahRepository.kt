package com.deenislam.sdk.service.repository.quran

import android.util.Log
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.api.QuranService
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal  class SurahRepository (
    private val deenService: DeenService?,
    private val quranService: QuranService?
):ApiCall {

    suspend fun fetchSurahList(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language",language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.surahList(parm = requestBody)

    }

    suspend fun fetchSurahList_quran_com(language:String) = makeApicall {
        quranService?.getSurahList(language)
    }
}