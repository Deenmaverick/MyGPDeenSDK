package com.deenislam.sdk.service.repository;

import android.util.Log
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
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

    suspend fun userSessionTrack(msisdn:String,sessionStart:Long,SessionEnd:Long) = makeApicall {

        val body = JSONObject()
        body.put("msisdn", msisdn)
        body.put("sessionStart", sessionStart)
        body.put("sessionEnd", SessionEnd)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        authenticateService?.userSessionTrack(parm = requestBody)

        Log.e("MyBLSDK","OKK")
    }

    suspend fun saveAdvertisementrecord(adID:Int,response:String) = makeApicall {

        val body = JSONObject()
        body.put("adID", adID)
        body.put("response", response)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        authenticateService?.saveAdvertisementrecord(parm = requestBody)

    }
} 