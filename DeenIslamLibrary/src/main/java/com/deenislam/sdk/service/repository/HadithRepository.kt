package com.deenislam.sdk.service.repository

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.HadithService
import com.deenislam.sdk.service.network.api.HadithServiceTest

internal class HadithRepository (
    private val hadithService: HadithService?,
    private val hadithServiceTest: HadithServiceTest?
): ApiCall {

    private val API_KEY = "SqD712P3E82xnwOAEOkGd5JZH8s9wRR24TqNFzjk"
    suspend fun getHadithCollection(language:String,page:Int,limit:Int) =
        makeApicall {
            hadithService?.getCollectionList(API_KEY, language = language, page = page, limit = limit)
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