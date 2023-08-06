package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.quran.juz.JuzResponse
import com.deenislam.sdk.service.network.response.quran.qurannew.surah.SurahList
import com.deenislam.service.network.response.quran.verses.Verses
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface QuranService {

    @GET("chapters")
    suspend fun getSurahList(@Query("language") language: String,): SurahList

    @GET("verses/by_chapter/{chapter_number}")
    suspend fun getVersesByChapter(
        @Path("chapter_number") chapter_number:Int,
        @Query("language") language: String,
        @Query("words") words: Boolean,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("translations") translations:String = "131,161",
        @Query("word_fields") word_fields:String = "qpcUthmaniHafs",
        @Query("audio") audio:Int = 7

    ): Verses

    @GET("juzs")
    suspend fun getJuzList(): JuzResponse

    @GET("verses/by_juz/{juz_number}")
    suspend fun getVersesByJuz(
        @Path("juz_number") juz_number:Int,
        @Query("language") language: String,
        @Query("words") words: Boolean,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int,
        @Query("translations") translations:String = "131,161",
        @Query("word_fields") word_fields:String = "qpcUthmaniHafs",
        @Query("audio") audio:Int = 1
    ): Verses

}