package com.deenislam.sdk.service.repository

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.HadithService
import com.deenislam.sdk.service.network.api.HadithServiceTest
import com.deenislam.sdk.utils.RequestBodyMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

internal class HadithRepository (
    private val hadithService: HadithService?,
    private val hadithServiceTest: HadithServiceTest?
): ApiCall {

    private val API_KEY = "SqD712P3E82xnwOAEOkGd5JZH8s9wRR24TqNFzjk"
    suspend fun getHadithCollection(language:String) =
        makeApicall {

            val body = JSONObject()
            body.put("language", language)
            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

            hadithService?.getCollectionList(parm = requestBody)
        }

    suspend fun getChapterByCollection(language:String,collectionName:String,page:Int,limit:Int) =
        makeApicall {
            hadithService?.getChapterByCollection(
                API_KEY,
                language = language,
                collectionName = collectionName,
                page = page,
                limit = limit
            )
        }

    // hadith preview

   suspend fun getHadithPreview(language: String, bookname:String, chapter: Int) =
        makeApicall {
            hadithServiceTest?.getHadithPreview(
                language = language,
                bookname = bookname,
                chapter = chapter
            )
        }
}