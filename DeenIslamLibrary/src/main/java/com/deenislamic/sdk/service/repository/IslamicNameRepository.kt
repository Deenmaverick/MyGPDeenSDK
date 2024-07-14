package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.IslamicNameService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
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

    suspend fun getIslamicNames(gender:String,language:String,alphabet:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("gender",gender)
        body.put("alphabet",alphabet)
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


    suspend fun getIslamicNameCats(gender: String,language:String) = makeApicall {

        val body = JSONObject()
        body.put("gender", gender)
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.getIslamicNameCategories(parm = requestBody)
    }

    suspend fun getIslamicNamesByCatId(id:Int) = makeApicall {

        val body = JSONObject()
        body.put("Id",id)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.getIslamicNamesByCatId(parm = requestBody)

    }

    suspend fun getIslamicNamesPatch(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        islamicNameService?.getIslamicNamesPatch(parm = requestBody)

    }

} 