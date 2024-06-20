package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.AuthenticateService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class DashboardRepository(
    private val authenticateService: AuthenticateService?
) : ApiCall {

    suspend fun getDashboardData(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.getDashboardDataV3(parm = requestBody)

    }

    suspend fun getAdData(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.getAdvertisement(parm = requestBody)

    }

    suspend fun saveAdvertisementrecord(adID:Int,response:String) = makeApicall {

        val body = JSONObject()
        body.put("adID", adID)
        body.put("response", response)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.saveAdvertisementrecord(parm = requestBody)

    }

} 