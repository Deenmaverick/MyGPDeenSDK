package com.deenislam.sdk.service.repository.quran

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal class AlQuranRepository(
    private val deenService: DeenService
): ApiCall {

    suspend fun fetchSurahdetails(surahID: Int, language: String, page: Int, contentCount: Int) = makeApicall {

        val body = JSONObject()
        body.put("order",surahID)
        body.put("language",language)
        body.put("page",page)
        body.put("contentCount",contentCount)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService.surahDetails(parm = requestBody)

    }
}