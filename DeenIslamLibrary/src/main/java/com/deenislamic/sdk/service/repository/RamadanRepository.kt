package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class RamadanRepository (
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getOtherRamadanTime(location:String,language:String) = makeApicall {

        val body = JSONObject()
        body.put("location", location)
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getOtherRamadanTime(parm = requestBody)
    }

    suspend fun getRamadanTime(location: String, language: String, date: String?) = makeApicall {

        val body = JSONObject()
        body.put("location", location)
        body.put("language", language)
        date?.let {
            body.put("firstDate", it)
        }
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getRamadanTime(parm = requestBody)
    }

    suspend fun getRamadanPatch(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getRamadanPatch(parm = requestBody)
    }

    suspend fun setRamadanTrack(isFasting: Boolean, language:String) = makeApicall {

        val body = JSONObject()
        body.put("Suhoor", isFasting)
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.setRamadanTrack(parm = requestBody)
    }

    suspend fun getRamadanCalendar(date: String, language:String) = makeApicall {

        val body = JSONObject()
        body.put("Date", date)
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getRamadanCalendar(parm = requestBody)
    }

} 