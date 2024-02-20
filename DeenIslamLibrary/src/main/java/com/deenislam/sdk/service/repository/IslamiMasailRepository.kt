package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.response.islamimasail.questionbycat.Data
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class IslamiMasailRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getMasailCat(language:String,page:Int,content:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("page", page)
        body.put("content", content)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getMasailCat(parm = requestBody)

    }

    suspend fun getMasailQuestionByCat(language:String,page:Int,content:Int,catid:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("page", page)
        body.put("content", content)
        body.put("Id", catid)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getMasailQuestionByCat(parm = requestBody)

    }

    suspend fun questionBookmar(language: String, getdata: Data) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("ContentId",getdata.Id)
        body.put("isFavorite",!getdata.IsFavorite)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.masailQuestionBookmar(parm = requestBody)

    }

    suspend fun getFavList(language: String,page:Int,content:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("page",page)
        body.put("content",content)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getMasailFavList(parm = requestBody)

    }

    suspend fun postQuestion(language: String, catid: Int, title: String,place:String,isAnonymous:Boolean,isUrgent:Boolean) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("categoryId",catid)
        body.put("title",title)
        body.put("place",place)
        body.put("isAnonymous",isAnonymous)
        body.put("isUrgent",isUrgent)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.postMasailQuestion(parm = requestBody)

    }

    suspend fun getMyMasailQuestionList(language: String,page:Int,content:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("page",page)
        body.put("content",content)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getMyMasailQuestionList(parm = requestBody)

    }

    suspend fun getHomePatch(language: String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.GetMasailHomePatch(parm = requestBody)

    }

    suspend fun getAnswer(id: Int) = makeApicall {

        val body = JSONObject()
        body.put("Id", id)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getMasailAnswer(parm = requestBody)

    }

} 