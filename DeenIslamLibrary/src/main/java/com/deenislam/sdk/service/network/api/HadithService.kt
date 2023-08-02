package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.hadith.HadithResponse
import com.deenislam.sdk.service.network.response.hadith.chapter.HadithChapterResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface HadithService {

    @POST("Hadith/AllHadithBook")
    suspend fun getCollectionList(
        @Body parm: RequestBody
    ): HadithResponse

    @GET("collections/{collectionName}/books")
    suspend fun getChapterByCollection(
        @Header("X-API-Key") key:String,
        @Path("collectionName") collectionName: String,
        @Query("language") language: String,
        @Query("page") page: Int=1,
        @Query("limit") limit: Int=50,
    ): HadithChapterResponse

}