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


    // juz list

    suspend fun fetchJuzList() = makeApicall {
        quranService?.getJuzList()
    }



    // Al-Quran GM

    suspend fun getQuranHomePatch(language: String) = makeApicall {

        val body = JSONObject()
        body.put("language",language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getQuranHomePatch(parm = requestBody)
    }

    suspend fun getSurahList(language: String, page: Int, contentCount: Int) = makeApicall {

        val body = JSONObject()
        body.put("language",language)
        body.put("page",page)
        body.put("content",contentCount)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getSurahList(parm = requestBody)
    }

    suspend fun getParaList(language: String, page: Int, contentCount: Int) = makeApicall {

        val body = JSONObject()
        body.put("language",language)
        body.put("page",page)
        body.put("content",contentCount)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getParaList(parm = requestBody)
    }

    suspend fun getVersesByChapter(
        language: String,
        page: Int,
        contentCount: Int,
        chapter_number:Int,
        isReadingMode: Boolean
    ) = makeApicall {

        val body = JSONObject()
        body.put("SurahId",chapter_number)
        body.put("language",language)
        body.put("page",page)
        body.put("content",contentCount)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        if(isReadingMode)
            deenService?.getReadingVersesByChapter(parm = requestBody)
        else
            deenService?.getVersesByChapter(parm = requestBody)

    }

    suspend fun getVersesByJuz(language: String, page: Int, contentCount: Int,juz_number:Int,isReadingMode: Boolean) = makeApicall {

        val body = JSONObject()
        body.put("SurahId",juz_number)
        body.put("language",language)
        body.put("page",page)
        body.put("content",contentCount)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        if(isReadingMode)
            deenService?.getReadingVersesByPara(parm = requestBody)
        else
            deenService?.getVersesByPara(parm = requestBody)
    }

    suspend fun updateFavAyat(language: String, ContentId:Int,isFav:Boolean) = makeApicall {

        val body = JSONObject()
        body.put("ContentId",ContentId)
        body.put("language",language)
        body.put("isFavorite",!isFav)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.updateFavAyat(parm = requestBody)
    }

    suspend fun getTafsir(surahID: Int, verseID: Int, ayatArabic: String, arabicFont: Int) = makeApicall {

        val body = JSONObject()
        body.put("SurahId",surahID)
        body.put("VerseID",verseID)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        deenService?.getTafsir(parm = requestBody)
    }

}