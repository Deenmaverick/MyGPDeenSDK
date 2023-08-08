package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal class UserTrackRepository(
    private val authenticateService:AuthenticateService?
) : ApiCall {

    suspend fun userTrack(language:String,msisdn:String,pagename:String,trackingID:Long,device:String="sdk") = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("msisdn", msisdn)
        body.put("pagename", pagename)
        body.put("trackingID", trackingID)
        body.put("device", device)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        authenticateService?.userTrack(parm = requestBody)
    }
} 