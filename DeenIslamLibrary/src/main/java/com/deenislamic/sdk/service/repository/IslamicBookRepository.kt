package com.deenislamic.sdk.service.repository

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class IslamicBookRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getIslamicBookHome(language: String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getIslamicBookHome(parm = requestBody)
    }

    suspend fun getBookAuthors(language:String, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBookAuthors(requestBody)
        }

    suspend fun getBookCategory(language:String, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBookCategory(requestBody)
        }

    suspend fun getBookItemByAuthors(Id: Int, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("Id", Id)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBookItemByAuthors(requestBody)
        }

    suspend fun getBookItemByCategory(Id: Int, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("Id", Id)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getBookItemByCategory(requestBody)
        }

    suspend fun makeBookFavorite(ContentId: String, isFavorite:Boolean, language:String) =
        makeApicall {
            val body = JSONObject()
            body.put("ContentId", ContentId)
            body.put("isFavorite", isFavorite)
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.makeBookFavorite(requestBody)
        }

    suspend fun getFavouriteBooks(language: String, page:Int, limit:Int) =
        makeApicall {
            val body = JSONObject()
            body.put("language", language)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            deenService?.getFavouriteBooks(requestBody)
        }
}