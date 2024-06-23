package com.deenislamic.sdk.service.repository

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class DailyDuaRepository (
    private val deenService: DeenService?
): ApiCall {

    suspend fun getAllDuaCategory(language:String) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.duaAllCategory(parm = requestBody)

        }


    suspend fun getAllDuaPatch(language:String) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.duaAllPatch(parm = requestBody)

        }

    suspend fun getDuaByCategory(cat:Int,language: String) =
        makeApicall {

            val body = JSONObject()
            body.put("category", cat)
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.duaByCategory(parm = requestBody)

        }

    suspend fun getfavDua(language: String) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getFavDua(parm = requestBody)

        }

    suspend fun setFavDua(isfav: Boolean, duaId: Int, language: String) =
        makeApicall {

            val body = JSONObject()
            body.put("contentId", duaId)
            body.put("isFavorite",!isfav)
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.setFavDua(parm = requestBody)

        }

    suspend fun getTodayDya(language: String) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getTodayDua(parm = requestBody)

        }
}