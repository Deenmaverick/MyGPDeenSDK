package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class IslamicEducationVideoRepository(
    private val deenService: DeenService?
) :
    ApiCall {
    suspend fun getIslamicEducationVideos(language: String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getIslamicEducationVideos(parm = requestBody)
    }

    suspend fun addIslamicContentHistory(category: Int, content: Int, language: String) = makeApicall {

        val body = JSONObject()
        body.put("catrgory", category)
        body.put("content", content)
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.addIslamicContentHistory(parm = requestBody)
    }

    suspend fun getDuaAmolHome(language: String,date:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("firstDate", date)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getDuaAmolHome(parm = requestBody)
    }
} 