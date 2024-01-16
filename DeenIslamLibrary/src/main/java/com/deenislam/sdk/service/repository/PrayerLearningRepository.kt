package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class PrayerLearningRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getPrayerLeareningAllCategory(language:String) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPrayerLearningAllCategory(parm = requestBody)

    }

    suspend fun getPrayerLeareningVisualization(language:String,gender:String) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put("gender", gender)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPrayerLearningVisual(parm = requestBody)

    }

    suspend fun getPrayerLeareningSubCat(language:String,cat:Int) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put("category", cat)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPrayerLearningSubCat(parm = requestBody)

    }
} 