package com.deenislamic.sdk.service.repository

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
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