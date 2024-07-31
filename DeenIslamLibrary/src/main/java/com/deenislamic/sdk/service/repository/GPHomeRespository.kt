package com.deenislamic.sdk.service.repository;

import android.util.Log
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.AuthenticateService
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class GPHomeRespository(
    private val authenticateService: AuthenticateService?,
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getGPHome(location:String) = makeApicall {

        val body = JSONObject()
        body.put("language", DeenSDKCore.GetDeenLanguage())
        body.put("Location", location)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.getGPHome(parm = requestBody)
    }

    suspend fun setPrayerTimeTrack(language: String,prayer_tag: String,isPrayed:Boolean) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put(prayer_tag.lowercase(), isPrayed)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.setPrayerTimeTrack(parm = requestBody)
    }

} 