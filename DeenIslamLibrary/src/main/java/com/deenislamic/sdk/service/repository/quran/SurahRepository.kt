package com.deenislamic.sdk.service.repository.quran

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.service.network.api.QuranService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
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