package com.deenislamic.sdk.service.repository

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class IslamicCalendarEventRepository(
    private val deenService: DeenService?
): ApiCall {
    suspend fun getIslamicCalendarEvent(language: String) = makeApicall {
        val body = JSONObject()
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getIslamicCalendarEvent(parm = requestBody)
    }

    suspend fun getIslamicCalendar(date:String,language: String) = makeApicall {
        val body = JSONObject()
        body.put("Date", date)
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getIslamicCalendar(parm = requestBody)
    }
}