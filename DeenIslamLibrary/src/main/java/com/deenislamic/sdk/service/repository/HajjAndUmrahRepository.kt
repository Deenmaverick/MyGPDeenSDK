package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class HajjAndUmrahRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getMakkahLiveVides(language:String) = makeApicall{
        val body = JSONObject()
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getMakkahLiveVideoes(parm = requestBody)

    }

    suspend fun getHajjAndUmrahPatch(language:String) = makeApicall{
        val body = JSONObject()
        body.put("device", "sdk")
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getHajjAndUmrahPatch(parm = requestBody)

    }

    suspend fun updateHajjMapTracker(mapTag:String,isTrack:Boolean,language:String) = makeApicall{
        val body = JSONObject()
        body.put(mapTag, isTrack)
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.updateHajjMapTracker(parm = requestBody)

    }

} 