package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class PrayerLearningRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getPrayerLeareningAllCategory(language:String) = makeApicall {
        val body = JSONObject()
        body.put("language", language)
        body.put("device", "android")
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