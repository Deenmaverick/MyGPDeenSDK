package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class DashboardRepository(
    private val authenticateService: AuthenticateService?
) : ApiCall {

    suspend fun getDashboardData(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.getDashboardData(parm = requestBody)

    }

} 