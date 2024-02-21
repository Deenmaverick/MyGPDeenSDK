package com.deenislam.sdk.service.repository

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class BoyanRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getBoyanHome(language: String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getBoyanHome(parm = requestBody)
    }

    suspend fun getBoyanCategories(language:String, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBoyanCategories(requestBody)
        }

    suspend fun getBoyanScholars(language:String, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBoyanScholars(requestBody)
        }

    suspend fun getBoyanVideoPreview(Id: Int, page: Int, limit: Int) =
        makeApicall {
            val body = JSONObject()
            body.put("Id", Id)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBoyanVideoPreview(requestBody)
        }

    suspend fun getBoyanCategoryVideoPreview(Id: Int, page: Int, limit: Int) =
        makeApicall {
            val body = JSONObject()
            body.put("Id", Id)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBoyanCategoryVideoPreview(requestBody)
        }

}