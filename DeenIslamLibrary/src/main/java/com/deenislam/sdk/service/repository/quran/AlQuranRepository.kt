package com.deenislam.sdk.service.repository.quran

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.api.QuranService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class AlQuranRepository(
    private val deenService: DeenService?,
    private val quranService: QuranService?
): ApiCall {

    suspend fun fetchSurahdetails(surahID: Int, language: String, page: Int, contentCount: Int) = makeApicall {

        val body = JSONObject()
        body.put("order",surahID)
        body.put("language",language)
        body.put("page",page)
        body.put("contentCount",contentCount)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.surahDetails(parm = requestBody)

    }

    suspend fun getVersesByChapter(language: String, page: Int, contentCount: Int,chapter_number:Int) = makeApicall {

        quranService?.getVersesByChapter(language = language, words = true, page = page, per_page = contentCount, chapter_number = chapter_number)
    }

    // juz list

    suspend fun fetchJuzList() = makeApicall {
        quranService?.getJuzList()
    }

    suspend fun getVersesByJuz(language: String, page: Int, contentCount: Int,juz_number:Int) = makeApicall {

        quranService?.getVersesByJuz(language = language, words = true, page = page, per_page = contentCount, juz_number = juz_number)
    }
}