package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.IslamicNameService
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal class IslamicNameRepository (
    private val islamicNameService: IslamicNameService?
) : ApiCall {

    suspend fun getAllFavNames(gender:String,language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("gender",gender)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.getFavNames(parm = requestBody)

    }

    suspend fun removeFavName(contentId: Int, language:String) = makeApicall{

        val body = JSONObject()
        body.put("language", language)
        body.put("contentId",contentId)
        body.put("isFavorite",false)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.modifyFavName(parm = requestBody)
    }

    suspend fun getIslamicNames(gender:String,language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("gender",gender)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.getIslamicNames(parm = requestBody)

    }

    suspend fun modifyFavNames(contentId: Int, language:String,isFav:Boolean) = makeApicall{

        val body = JSONObject()
        body.put("language", language)
        body.put("contentId",contentId)
        body.put("isFavorite",isFav)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.modifyFavName(parm = requestBody)
    }
} 