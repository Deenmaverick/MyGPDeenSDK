package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
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