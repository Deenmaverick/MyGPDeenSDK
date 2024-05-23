package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class QurbaniRepository (
    private val deenService: DeenService
) : ApiCall {

    suspend fun getQurabniPatch(language:String) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("device", "sdk")
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService.getQurbaniPatch(parm = requestBody)

        }

    suspend fun getCoantentByCat(cat:Int,language:String) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("Category", cat)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService.getQurbaniContentByCat(parm = requestBody)

        }

} 