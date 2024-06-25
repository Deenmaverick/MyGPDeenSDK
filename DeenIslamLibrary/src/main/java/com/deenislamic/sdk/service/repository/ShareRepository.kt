package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class ShareRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getWallpaperCat(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getWallpaperCat(parm = requestBody)

    }

    suspend fun getWallpaper(language:String,catid:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("Category", catid)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getWallpaper(parm = requestBody)

    }

} 