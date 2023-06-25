package com.deenislam.sdk.service.repository

import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal class PrayerCalendarRespository(
    private val deenService: DeenService?
):ApiCall {

    suspend fun getMonthlyData(localtion:String, language:String) = makeApicall {
        val body = JSONObject()
        body.put("location",localtion)
        body.put("language",language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService!!.currentMonthPrayerTime(parm = requestBody)

    }
}