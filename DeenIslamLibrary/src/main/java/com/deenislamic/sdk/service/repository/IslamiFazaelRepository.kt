package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class IslamiFazaelRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getAllFazael(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getAllIslamiFazael(parm = requestBody)


    }

    suspend fun getFazaelByCat(language:String,catid:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("Id", catid)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getFazaelByCat(parm = requestBody)


    }
} 