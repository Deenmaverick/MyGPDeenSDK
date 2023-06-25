package com.deenislam.sdk.service.repository.quran

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SurahRepository(
    private val deenService: DeenService?
):ApiCall {

    suspend fun fetchSurahList(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language",language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService!!.surahList(parm = requestBody)

    }
}