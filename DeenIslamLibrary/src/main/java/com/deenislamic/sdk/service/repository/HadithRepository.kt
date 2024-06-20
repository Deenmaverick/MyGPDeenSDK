package com.deenislamic.sdk.service.repository

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.HadithService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class HadithRepository (
    private val hadithService: HadithService?
): ApiCall {

    suspend fun getHadithCollection(language:String) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            hadithService?.getCollectionList(parm = requestBody)
        }

    suspend fun getChapterByCollection(language:String,bookId:Int,page:Int,limit:Int) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            body.put("bookId", bookId)
            body.put("page", page)
            body.put("content", limit)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            hadithService?.getChapterByCollection(requestBody)
        }

    // hadith preview

   suspend fun getHadithPreview(language: String, bookId:Int, chapterId: Int,page:Int,limit:Int) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            body.put("bookId", bookId)
            body.put("chapterId", chapterId)
            body.put("page", page)
            body.put("content", limit)

            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            hadithService?.getHadithPreview(parm = requestBody)
        }

    suspend fun setHadithFav(isfav: Boolean, hadithID: Int, language: String) =
        makeApicall {

            val body = JSONObject()
            body.put("contentId", hadithID)
            body.put("isFavorite",!isfav)
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            hadithService?.setHadithFav(parm = requestBody)

        }

    suspend fun getFavHadith(language: String,page:Int,limit:Int) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            body.put("page", page)
            body.put("content", limit)

            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            hadithService?.getFavHadith(parm = requestBody)
        }
}